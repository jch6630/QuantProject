package com.jch.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.jch.DTO.NaverNewsApi;

public class RequestNaverNewsApi {

	private final NaverNewsApi nna = new NaverNewsApi();

	public String searchNews(String keyword) throws Exception {
		String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
		String requestUrl = nna.getApiUrl() + encodedKeyword + "&display=10&sort=date";

		// HttpURLConnection을 try-with-resources로 관리하여 자동 반환
		HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.setRequestProperty("X-Naver-Client-Id", nna.getClientId());
			connection.setRequestProperty("X-Naver-Client-Secret", nna.getClientSecret());

			int responseCode = connection.getResponseCode();

			// BufferedReader도 try-with-resources 사용하여 자동 반환
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream()
							: connection.getErrorStream()))) {

				StringBuilder response = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					response.append(line);
				}

				return response.toString(); // JSON 형식으로 반환
			}
		} finally {
			connection.disconnect(); // 자원 반환
		}
	}
}
