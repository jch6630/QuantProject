package com.jch.util;

import java.util.logging.Logger;

public class LoggerUtil {
	private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());

	// 로그 메시지에 ID 추가
	public static void logInfo(String message, String id) {
		logger.info("[ID: " + id + "] " + message);
	}

	public static void logWarning(String message, String id) {
		logger.warning("[ID: " + id + "] " + message);
	}

	public static void logSevere(String message, String id) {
		logger.severe("[ID: " + id + "] " + message);
	}

	public static void log(String string) {
		logger.severe("[화면 출력 자료] " + string);
	}
}
