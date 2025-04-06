package com.jch.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jch.request.RequestGeminiApi; // Ensure this class exists

public class NewsAnalyze {

	private static final int MAX_RETRIES = 3;
	private static final int MAX_REQUESTS_PER_MINUTE = 15; // Adjust as needed, based on your Gemini account
	private static long lastRequestTime = 0;
	private static final Object lock = new Object();

	// GeminiAI로 뉴스 분석
	public String[] newsAnalyze(String stockName, String stockCode, ArrayList<String> news) {

		String[] returnResult = new String[3];
		returnResult[0] = stockName; // 기업명
		returnResult[1] = stockCode; // 주식코드
		returnResult[2] = ""; // 분석결과

		RequestGeminiApi rga = new RequestGeminiApi();
		// RequestNowStockPrice rnsp = new RequestNowStockPrice(); // Unused - remove

		try {
			String order = generateApiRequest(news, stockName);

			String result = callGeminiWithRetry(rga, order, MAX_RETRIES);

			if (result == null) {
				returnResult[2] = "API Failed After Retries";
				return returnResult;
			}

			// Gson 객체 생성
			Gson gson = new Gson();

			// JSON 파싱하여 필요한 부분만 추출
			JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
			JsonArray candidates = jsonObject.getAsJsonArray("candidates");

			if (candidates != null && candidates.size() > 0) {
				// "content" → "parts" → "text" 내부 JSON 추출
				JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
				JsonArray parts = content.getAsJsonArray("parts");
				String innerJson = parts.get(0).getAsJsonObject().get("text").getAsString();

				// Extract the actual result code using regex
				Pattern pattern = Pattern.compile("<Result: (1|2)>");
				Matcher matcher = pattern.matcher(innerJson);

				if (matcher.find()) {
					returnResult[2] = matcher.group(0); // Return the entire tag
				} else {
					returnResult[2] = "Failed to parse Result tag from API response: " + innerJson;
				}

				return returnResult;
			} else {
				returnResult[2] = "Failed: No candidates in API response";
				return returnResult;
			}
		} catch (IOException e) {
			System.err.println("❌ API 요청 중 오류 발생: " + e.getMessage());
			e.printStackTrace();
			returnResult[2] = "Failed: IO Exception " + e.getMessage();
			return returnResult;
		} catch (InterruptedException e) {
			System.err.println("❌ 스레드 interrupted: " + e.getMessage());
			e.printStackTrace();
			returnResult[2] = "Failed: Interrupted Exception " + e.getMessage();
			Thread.currentThread().interrupt(); // Restore interrupted state
			return returnResult;
		} catch (Exception e) {
			System.err.println("❌ General Exception: " + e.getMessage());
			e.printStackTrace();
			returnResult[2] = "Failed: General Exception " + e.getMessage();
			return returnResult;
		}
	}

	// Wrapped for retry logic
	private String callGeminiWithRetry(RequestGeminiApi rga, String order, int maxRetries)
			throws IOException, InterruptedException {
		int retryCount = 0;
		long delay = 1000; // Initial delay in milliseconds

		while (retryCount < maxRetries) {
			try {
				return callGeminiWithRateLimit(rga, order); // Make sure this is properly rate-limited
			} catch (IOException e) {
				if (e.getMessage().contains("429") || e.getMessage().contains("RESOURCE_EXHAUSTED")) {
					retryCount++;
					System.err.println("Rate limit exceeded (or other API error). Retrying in " + delay + "ms (attempt "
							+ retryCount + "/" + maxRetries + ")");
					TimeUnit.MILLISECONDS.sleep(delay);
					delay *= 2; // Exponential backoff
				} else {
					// Other IOException, re-throw
					throw e;
				}
			}
		}

		System.err.println("❌ Max retries reached. API call failed after " + maxRetries + " attempts.");
		return null; // Indicate total failure
	}

	private String callGeminiWithRateLimit(RequestGeminiApi rga, String order)
			throws IOException, InterruptedException {
		synchronized (lock) {
			long currentTime = System.currentTimeMillis();
			long timeSinceLastRequest = currentTime - lastRequestTime;

			// Calculate the time to wait to respect the rate limit
			double waitTime = (60 * 1000.0 / MAX_REQUESTS_PER_MINUTE) - timeSinceLastRequest;

			if (waitTime > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep((long) waitTime);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore interrupted state
					throw new InterruptedException(
							"Thread interrupted while waiting for rate limit: " + e.getMessage());

				}
			}

			// Perform the API call
			try {
				String result = rga.analyzeNews(order);
				lastRequestTime = System.currentTimeMillis(); // Update the last request time
				return result;
			} catch (IOException e) {
				throw new IOException("API call failed: " + e.getMessage(), e);
			}


		}
	}

	private static String generateApiRequest(List<String> newsLinks, String stockName) {
		StringBuilder sb = new StringBuilder();

		sb.append("Analyze the following news articles about " + stockName
				+ " and determine the overall sentiment towards its stock price. "
				+ "Consider factors such as revenue, profit, market share, and competitor analysis if mentioned in the articles.  "
				+ "Return ONLY '<Result: 1>' if the OVERALL sentiment is clearly positive and strongly suggests a future stock price increase. "
				+ "Return ONLY '<Result: 2>' if the OVERALL sentiment is negative or neutral, or if any article is inaccessible or too ambiguous to assess. Do not provide any additional explanation or text. ONLY return the tag.\n\n");

		sb.append("News Articles:\n");

		for (String link : newsLinks) {
			sb.append("* ").append(link).append("\n");
		}

		return sb.toString();
	}
}