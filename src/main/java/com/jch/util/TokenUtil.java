package com.jch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jch.DTO.KoreaStockApi;

public class TokenUtil {

    // 로그 객체 생성
    private static final Logger logger = Logger.getLogger(TokenUtil.class.getName());

    // 파일 경로 설정 (윈도우의 Quant 폴더 안에 Token.txt 파일)
    private static final String TOKEN_FILE_PATH = System.getProperty("user.home") + "\\Quant\\Token.txt";

	private static final HttpClient client = HttpClient.newBuilder().build();

    // 토큰 및 시간을 파일에 저장하는 메소드
    public static void saveToken(String token) {
        try {
            // 폴더와 파일이 존재하는지 확인하고 없으면 생성
            File folder = new File(System.getProperty("user.home") + "\\Quant");
            if (!folder.exists()) {
                folder.mkdir(); // 폴더가 없다면 생성
                logger.info("[saveToken] 폴더가 존재하지 않아 생성되었습니다.");
            }

            // 파일에 토큰과 현재 시간 저장
            FileWriter writer = new FileWriter(TOKEN_FILE_PATH);
            LocalDateTime currentTime = LocalDateTime.now();
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(token + "\n" + formattedTime);
            writer.close();

            logger.info("[saveToken] 토큰과 시간이 정상적으로 저장되었습니다.");
        } catch (IOException e) {
            logger.severe("[saveToken] 파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 파일에서 토큰 및 시간을 읽어오는 메소드
    public static String[] readToken() {
        try {
            // 파일 존재 여부 체크
            File file = new File(TOKEN_FILE_PATH);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String token = reader.readLine();
                String time = reader.readLine();
                reader.close();
                logger.info("[readToken] token : "+token);
                logger.info("[readToken] time : "+time);
                logger.info("[readToken] 파일에서 토큰과 시간을 정상적으로 읽었습니다.");
                return new String[] { token, time };
            } else {
                logger.warning("[readToken] 파일이 존재하지 않습니다.");
            }
        } catch (IOException e) {
            logger.severe("[readToken] 파일 읽기 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // 파일이 없거나 오류 발생 시 null 반환
    }

    // 현재 시간과 파일에서 읽어온 시간 차이를 비교하여 3시간 이상인지 확인
    public static boolean isTokenExpired(String fileTime) {
        try {
            LocalDateTime fileDateTime = LocalDateTime.parse(fileTime,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime currentTime = LocalDateTime.now();
            // 3시간 전 시간과 비교
            if (fileDateTime.plusHours(3).isBefore(currentTime)) {
                logger.info("[isTokenExpired] 토큰이 만료되었습니다. (" + fileDateTime + " ~ " + currentTime + ")");
                return true;
            } else {
                logger.info("[isTokenExpired] 토큰이 유효합니다. (" + fileDateTime + " ~ " + currentTime + ")");
                return false;
            }
        } catch (Exception e) {
            logger.severe("[isTokenExpired] 시간 비교 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return true; // 오류 발생 시 기본적으로 만료된 것으로 간주
    }

	// 토큰 받기 메소드
	public static String token(KoreaStockApi ksa) {
		String tokenLogId = UUID.randomUUID().toString();
		String requestBody = "{ \"grant_type\": \"client_credentials\", \"appkey\": \"" + ksa.getAppKey()
				+ "\", \"appsecret\": \"" + ksa.getAppSecret() + "\" }";

		try {
			HttpRequest requestToken = HttpRequest.newBuilder().uri(new URI(ksa.getKsaUrl() + ksa.getTokenUrl()))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody))
					.build();

			HttpResponse<String> response = client.send(requestToken, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(response.body());
				String accessToken = rootNode.path("access_token").asText();

				if (accessToken.isEmpty()) {
					LoggerUtil.logWarning("Access Token이 비어있습니다.", tokenLogId);
					return "Failed";
				}
				LoggerUtil.logInfo("Token Get Successed", tokenLogId);
				return accessToken;
			} else {
				LoggerUtil.logWarning("Failed to get access token. Status code: " + response.statusCode(), tokenLogId);
				return "Failed";
			}
		} catch (Exception e) {
			LoggerUtil.logSevere("토큰 요청 중 오류 발생: " + e.getMessage(), tokenLogId);
			e.printStackTrace();
			return "Failed";
		}
	}
}
