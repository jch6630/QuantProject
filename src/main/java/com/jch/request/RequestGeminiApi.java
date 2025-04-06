package com.jch.request;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.jch.DTO.GeminiApi;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestGeminiApi {
	// OkHttpClient를 싱글톤으로 유지 (매번 생성하지 않음)
	private static final OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(Duration.ofSeconds(180))
			.readTimeout(Duration.ofSeconds(180)).writeTimeout(Duration.ofSeconds(180)).build();

	private static final int MAX_RETRIES = 3; // 최대 재시도 횟수
	private static final long RETRY_DELAY_MS = 2000; // 재시도 간격 (2초)

	public static String analyzeNews(String prompt) throws IOException {
		GeminiApi gmn = new GeminiApi();
		Gson gson = new Gson();

		// 요청 데이터 생성
		Map<String, Object> part = new HashMap<>();
		part.put("text", prompt);

		Map<String, Object> content = new HashMap<>();
		content.put("parts", new Object[] { part });

		Map<String, Object> generationConfig = new HashMap<>();
		generationConfig.put("temperature", "0.2");
		generationConfig.put("maxOutputTokens", "50");

		Map<String, Object> requestData = new HashMap<>();
		requestData.put("contents", new Object[] { content });
		requestData.put("generationConfig", generationConfig);

		RequestBody body = RequestBody.create(gson.toJson(requestData),
				MediaType.get("application/json; charset=utf-8"));
		System.out.println("요청 데이터 : " + gson.toJson(requestData));

		String uri = gmn.getGeminiUrl() + gmn.getGeminiKey();

		Request request = new Request.Builder().url(uri).post(body).addHeader("Content-Type", "application/json")
				.build();

		// try-with-resources 사용하여 Response 자동 반환
		int attempt = 0;

		while (attempt < MAX_RETRIES) {
			attempt++;
			try (Response response = CLIENT.newCall(request).execute()) {
				String responseBody = response.body().string();

				if (response.isSuccessful()) {
					return responseBody; // 성공하면 바로 반환
				}

				System.err.println("API 요청 실패 (시도 " + attempt + "/" + MAX_RETRIES + "): " + responseBody);
			} catch (IOException e) {
				System.err.println("네트워크 오류 발생 (시도 " + attempt + "/" + MAX_RETRIES + "): " + e.getMessage());
			}

			// 재시도 대기 시간 추가
			if (attempt < MAX_RETRIES) {
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // 현재 스레드의 인터럽트 상태 복구
					throw new IOException("재시도 중 인터럽트 발생", ie);
				}
			}
		}

		throw new IOException("API 요청이 " + MAX_RETRIES + "회 실패하였습니다.");
	}
}

