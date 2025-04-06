package com.jch.request;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.logging.Logger;

import com.jch.DTO.KoreaStockApi;
import com.jch.DTO.TokenJson;
import com.jch.util.LoggerUtil;
import com.jch.util.TokenUtil;

public class RequestEstimatedClosingPrice {

	private static final String LOG_ID = UUID.randomUUID().toString(); // 고유한 ID 생성
	private static final Logger logger = Logger.getLogger(RequestCheckTopTwenty.class.getName());

	private final KoreaStockApi ksa = new KoreaStockApi(); // KoreaStockApi 객체
	private final TokenJson tj = new TokenJson(); // TokenJson 객체
	private static final HttpClient client = HttpClient.newHttpClient(); // 싱글톤 HttpClient

	// 주식 데이터 요청 메소드
	public String request(String stockCode) {
		String requestLogId = UUID.randomUUID().toString(); // 각 요청마다 고유한 ID 생성

		// 파일에서 토큰 및 시간 읽어오기
		String[] tokenData = TokenUtil.readToken();
		String token = null;

		if (tokenData != null) {
			// 토큰과 시간 읽기
			token = tokenData[0];
			String fileTime = tokenData[1];
			LoggerUtil.logSevere("tokenData : " + tokenData[0], requestLogId); // 로그

			// 3시간 이상 경과한 경우 새 토큰 요청
			if (TokenUtil.isTokenExpired(fileTime)) {
				token = TokenUtil.token(ksa);
				if (!"Failed".equals(token)) {
					TokenUtil.saveToken(token);
				} else {
					LoggerUtil.logSevere("새 토큰 요청 실패", requestLogId);
					return "Failed";
				}
			}
		} else {
			token = TokenUtil.token(ksa);
			if (!"Failed".equals(token)) {
				TokenUtil.saveToken(token);
			} else {
				LoggerUtil.logSevere("토큰 요청 실패", requestLogId);
				return "Failed";
			}
		}

		if ("Failed".equals(token)) {
			LoggerUtil.logSevere("토큰을 받지 못했습니다.", requestLogId);
			return "Failed";
		}

		try {
			LoggerUtil.logInfo("API 요청 시작", requestLogId);
			LoggerUtil.logInfo("요청 URL: " + ksa.getKsaUrl() + ksa.getApiChkTopTwnUrl(), requestLogId);

			// 주식 검색 순위 20 요청 URL 생성
			String urlWithParams = ksa.getKsaUrl() + "/uapi/domestic-stock/v1/quotations/exp-price-trend"
					+ "?fid_cond_mrkt_div_code=J&fid_input_iscd=" + stockCode + "&fid_mkop_cls_code=0";

			// GET 요청 생성
			HttpRequest request = HttpRequest.newBuilder().uri(new URI(urlWithParams))
					.header("content-type", "application/json; charset=utf-8")
					.header("authorization", "Bearer " + token).header("appkey", ksa.getAppKey())
					.header("appsecret", ksa.getAppSecret()).header("tr_id", ksa.getTrIdECP())
					.header("custtype", "P")
					.GET().build();

			// 요청 전송 및 응답 처리
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// 응답 결과 확인
			if (response.statusCode() == 200) {
				LoggerUtil.logInfo("API Response Success", requestLogId);
				return response.body();
			} else {
				LoggerUtil.logWarning(
						"Failed to get top Estimated Closing Price data. Status code: " + response.statusCode(),
						requestLogId);
				return "Failed";
			}
		} catch (Exception e) {
			LoggerUtil.logSevere("주식 조회 상위 목록 요청 중 오류 발생: " + e.getMessage(), requestLogId);
			return "Failed";
		}
	}
}
