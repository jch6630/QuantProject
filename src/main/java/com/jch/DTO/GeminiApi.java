package com.jch.DTO;

public class GeminiApi {

	private static final String GEMINI_KEY = System.getenv("GEMINI_KEY");
	private static final String GEMINI_URL = System.getenv("GEMINI_URL");

	public static String getGeminiKey() {
		return GEMINI_KEY;
	}

	public static String getGeminiUrl() {
		return GEMINI_URL;
	}

}
