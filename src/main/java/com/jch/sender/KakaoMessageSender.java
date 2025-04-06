package com.jch.sender;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class KakaoMessageSender {

	public static void sendMessageToMyself(String message, String accessToken) {
		try {
			// 카카오톡 메시지 보내기 API URL
			URL url = new URL("https://kapi.kakao.com/v2/api/talk/memo/default/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// HTTP POST 요청 설정
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setDoOutput(true);


			// 메시지 내용 작성
//			String jsonBody = "template_object:{\"object_type\": \"text\",\"text\": \"텍스트 영역입니다. 최대 200자 표시 가능합니다.\",\"link\": {\"web_url\": \"https://jch.com\"}}";
			String jsonBody = "template_object={" + "\"object_type\": \"text\"," + "\"text\": \"" + message + "\","
					+ "\"link\": {" + "\"web_url\": \"https://jch.com/sendMe\","
					+ "\"mobile_web_url\": \"https://jch.com/sendMe\"" + "}," + "\"button_title\": \"바로 확인\"" + "}";

			// 요청 본문 전송
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = jsonBody.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			// 응답 코드 확인
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				System.out.println("메시지 전송 성공!");
			} else {
				System.out.println("메시지 전송 실패. 응답 코드: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

