package com.jch.DTO;

public class NaverNewsApi {
	private static final String CLIENT_ID = System.getenv("NVR_CLI_ID"); // 네이버 API Client ID
	private static final String CLIENT_SECRET = System.getenv("NVR_CLI_SEC"); // 네이버 API Client Secret
	private static final String API_URL = System.getenv("NVR_NEWS_URL");

	public static String getClientId() {
		return CLIENT_ID;
	}

	public static String getClientSecret() {
		return CLIENT_SECRET;
	}

	public static String getApiUrl() {
		return API_URL;
	}

	@Override
	public String toString() {
		return "NaverNewsApi [CLIENT_ID=" + CLIENT_ID + ", CLIENT_SECRET=" + CLIENT_SECRET + ", API_URL="
				+ API_URL + "]";
	}

}
