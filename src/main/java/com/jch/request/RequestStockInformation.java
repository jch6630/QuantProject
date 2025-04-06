package com.jch.request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.logging.Logger;

import com.jch.DTO.KoreaStockApi;
import com.jch.DTO.PositiveStock;
import com.jch.util.JsonParse;
import com.jch.util.LoggerUtil;
import com.jch.util.TokenUtil;

public class RequestStockInformation {
	private static final String LOG_ID = UUID.randomUUID().toString(); // 고유한 ID 생성
	private static final Logger logger = Logger.getLogger(RequestStockInformation.class.getName());

	private KoreaStockApi ksa = new KoreaStockApi(); // KoreaStockApi 객체

	private static final HttpClient client = HttpClient.newBuilder().build();

	// 주식 데이터 요청 메소드
	public PositiveStock request(String stockCode) {
		String requestLogId = UUID.randomUUID().toString(); // 각 요청마다 고유한 ID 생성

		PositiveStock psf = new PositiveStock("Failed", "Failed");

		// 파일에서 토큰 및 시간 읽어오기
		String[] tokenData = TokenUtil.readToken();
		String token = null;

		if (tokenData != null) {
			token = tokenData[0]; // 기존 토큰 가져오기
			String fileTime = tokenData[1];

			LoggerUtil.logSevere("tokenData : " + tokenData[0], requestLogId); // 토큰 로그 기록

			// 3시간 이상 경과한 경우 새 토큰을 받아옴
			if (TokenUtil.isTokenExpired(fileTime)) {
				token = TokenUtil.token(ksa); // 새 토큰 요청
				if (!"Failed".equals(token)) {
					TokenUtil.saveToken(token); // 새 토큰 저장
				} else {
					LoggerUtil.logSevere("새 토큰 요청 실패", requestLogId);
					return psf;
				}
			}
		} else {
			token = TokenUtil.token(ksa); // 파일에 토큰이 없으면 API 요청을 통해 토큰 받기
			if (!"Failed".equals(token)) {
				TokenUtil.saveToken(token); // 받은 토큰 저장
			} else {
				LoggerUtil.logSevere("토큰 요청 실패", requestLogId);
				return psf;
			}
		}

		if ("Failed".equals(token)) {
			LoggerUtil.logSevere("토큰을 받지 못했습니다.", requestLogId);
			return psf;
		}

		try {
			JsonParse jp = new JsonParse();

			String urlWithParams = ksa.getKsaUrl() + "/uapi/domestic-stock/v1/quotations/search-stock-info"
					+ "?PRDT_TYPE_CD=300&PDNO=" + stockCode;

			HttpRequest request = HttpRequest.newBuilder().uri(new URI(urlWithParams))
					.header("content-type", "application/json; charset=utf-8")
					.header("authorization", "Bearer " + token).header("appkey", ksa.getAppKey())
					.header("appsecret", ksa.getAppSecret()).header("tr_id", "CTPF1002R").header("custtype", "P").GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				LoggerUtil.logInfo("API Response Successed", requestLogId);

				return jp.siJsonParse(response.body(), stockCode);
			} else {
				LoggerUtil.logWarning("Failed to get stock price data. Status code: " + response.statusCode(),
						requestLogId);
				return psf;
			}
		} catch (Exception e) {
			LoggerUtil.logSevere("주식 시세 요청 중 오류 발생: " + e.getMessage(), requestLogId);
			e.printStackTrace();
			return psf;
		}
	}
}
