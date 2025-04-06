package com.jch.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KakaoTokenUtil {
	private static final String KAKAO_TOKEN_FILE = System.getProperty("user.home") + "\\Quant\\kakao_token.txt";
	private static final String KAKAO_CLIENT_ID = System.getenv("KAKAO_CLIENT_ID"); // 카카오 REST API 키
	private static final String KAKAO_REDIRECT_URI = System.getenv("KAKAO_REDIRECT_URI"); // 설정한 리디렉트 URI
	private static final String KAKAO_AUTH_CODE = System.getenv("KAKAO_AUTHORIZATION_CODE"); // 최초 1회 발급

public static String getToken() {
	System.out.println("KAKAO_TOKEN_FILE : " + KAKAO_TOKEN_FILE);
	System.out.println("KAKAO_CLIENT_ID : " + KAKAO_CLIENT_ID);
	System.out.println("KAKAO_REDIRECT_URI : " + KAKAO_REDIRECT_URI);
	System.out.println("KAKAO_AUTH_CODE : " + KAKAO_AUTH_CODE);

	try {
		JsonObject KAKAO_tokenData = KAKAO_loadTokenFromFile();

		if (KAKAO_tokenData == null || !KAKAO_tokenData.has("access_token")) {
			KAKAO_tokenData = KAKAO_getAccessToken(KAKAO_AUTH_CODE);
			KAKAO_saveTokenToFile(KAKAO_tokenData);
			System.out.println("토큰 최초 발급 완료!");
		} else {
			System.out.println("저장된 토큰 로드 완료!");
		}

		// Access Token이 만료되었을 경우 Refresh Token으로 갱신
		if (KAKAO_isTokenExpired(KAKAO_tokenData)) {
			System.out.println("Access Token이 만료됨. Refresh Token으로 갱신...");
			if (KAKAO_tokenData.has("refresh_token")) {
				KAKAO_tokenData = KAKAO_refreshAccessToken(KAKAO_tokenData.get("refresh_token").getAsString());
				KAKAO_saveTokenToFile(KAKAO_tokenData);
				System.out.println("토큰 갱신 완료!");
			} else {
				System.err.println("❌ Refresh Token이 없음. 새로운 Authorization Code 필요!");
				return null;
			}
		}

		return KAKAO_tokenData.toString();
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
}

// 🔹 최초 Access Token 발급
public static JsonObject KAKAO_getAccessToken(String KAKAO_authCode) throws IOException {
	String KAKAO_tokenUrl = "https://kauth.kakao.com/oauth/token";

	String KAKAO_params = "grant_type=authorization_code" + "&client_id="
			+ URLEncoder.encode(KAKAO_CLIENT_ID, "UTF-8") + "&redirect_uri="
			+ URLEncoder.encode(KAKAO_REDIRECT_URI, "UTF-8") + "&code="
			+ URLEncoder.encode(KAKAO_authCode, "UTF-8");

	return KAKAO_requestToken(KAKAO_tokenUrl, KAKAO_params);
}

// 🔹 Refresh Token을 사용해 Access Token 갱신
public static JsonObject KAKAO_refreshAccessToken(String KAKAO_refreshToken) throws IOException {
	String KAKAO_tokenUrl = "https://kauth.kakao.com/oauth/token";

	String KAKAO_params = "grant_type=refresh_token" + "&client_id=" + URLEncoder.encode(KAKAO_CLIENT_ID, "UTF-8")
			+ "&refresh_token=" + URLEncoder.encode(KAKAO_refreshToken, "UTF-8");

	JsonObject response = KAKAO_requestToken(KAKAO_tokenUrl, KAKAO_params);

	// Refresh Token도 새로 발급될 수 있으므로 업데이트
	if (response.has("refresh_token")) {
		response.addProperty("refresh_token", response.get("refresh_token").getAsString());
	}

	return response;
}

// 🔹 API 요청을 통해 토큰 발급
private static JsonObject KAKAO_requestToken(String KAKAO_tokenUrl, String KAKAO_params) throws IOException {
	URL url = new URL(KAKAO_tokenUrl);
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod("POST");
	conn.setDoOutput(true);
	conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	try (OutputStream os = conn.getOutputStream()) {
		os.write(KAKAO_params.getBytes());
		os.flush();
	}

	int responseCode = conn.getResponseCode();
	StringBuilder response = new StringBuilder();
	try (BufferedReader br = new BufferedReader(new InputStreamReader(
			responseCode == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream()))) {
		String line;
		while ((line = br.readLine()) != null) {
			response.append(line);
		}
	}

	if (responseCode == HttpURLConnection.HTTP_OK) {
		System.out.println("✅ 토큰 요청 성공: " + response);
		return JsonParser.parseString(response.toString()).getAsJsonObject();
	} else {
		System.err.println("❌ 토큰 요청 실패: HTTP " + responseCode + " - " + response);
		throw new IOException("토큰 요청 실패: HTTP " + responseCode + " - " + response.toString());
	}
}

// 🔹 토큰을 파일에 저장
public static void KAKAO_saveTokenToFile(JsonObject KAKAO_tokenData) throws IOException {
	KAKAO_tokenData.addProperty("token_timestamp", System.currentTimeMillis() / 1000); // 현재 시간 저장

	try (BufferedWriter writer = new BufferedWriter(new FileWriter(KAKAO_TOKEN_FILE))) {
		writer.write(KAKAO_tokenData.toString());
	}
}

// 🔹 저장된 토큰을 파일에서 불러오기
public static JsonObject KAKAO_loadTokenFromFile() throws IOException {
	File file = new File(KAKAO_TOKEN_FILE);
	if (!file.exists()) {
		return null;
	}

	try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
		return JsonParser.parseString(reader.readLine()).getAsJsonObject();
	}
}

// 🔹 Access Token이 만료되었는지 확인
public static boolean KAKAO_isTokenExpired(JsonObject KAKAO_tokenData) {
	if (!KAKAO_tokenData.has("expires_in") || !KAKAO_tokenData.has("token_timestamp")) {
		return true; // expires_in 값이 없으면 만료된 것으로 간주
	}

	long expiresIn = KAKAO_tokenData.get("expires_in").getAsLong(); // 초 단위
	long tokenTimestamp = KAKAO_tokenData.get("token_timestamp").getAsLong(); // 발급 시간
	long currentTime = System.currentTimeMillis() / 1000; // 현재 시간 (초 단위)

	return (currentTime - tokenTimestamp) >= expiresIn; // 남은 시간이 0초 이하이면 만료
}
}