package com.jch.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jch.DTO.EstimatedClosingPrice;
import com.jch.DTO.PSSOP;
import com.jch.DTO.PSSResponseBody;
import com.jch.DTO.PositiveStock;
import com.jch.DTO.ResponseNaverNews;
import com.jch.DTO.SellStockDTO;

public class JsonParse {

	public PSSResponseBody PSSJsonParse(String apiResult)
			throws JsonMappingException, JsonProcessingException {

		// JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(apiResult);
		LoggerUtil.log("json entered value : " + apiResult);

		// 현재 날짜와 시간 얻기
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String time = now.format(formatter);

		// JSON에서 필요한 데이터 추출
		String rt_cd = rootNode.get("rt_cd").asText();
		String msg_cd = rootNode.get("msg_cd").asText();
		String msg1 = rootNode.get("msg1").asText();
		JsonNode arrayNode = rootNode.get("output1");

		ArrayList<PSSOP> alPSSOP = new ArrayList<PSSOP>();
		for (JsonNode element : arrayNode) {
			String valueMarket = element.get("mrkt_div_cls_code").asText(); // 시장구분
			String valueStock = element.get("mksc_shrn_iscd").asText(); // 종목코드
			PSSOP pssop = new PSSOP(valueMarket, valueStock);
			alPSSOP.add(pssop);
		}

		PSSResponseBody PSSRB = new PSSResponseBody(rt_cd, msg_cd, msg1, alPSSOP);

		return PSSRB;
	}

	public ArrayList<ResponseNaverNews> nnJsonParse(String news) throws JsonMappingException, JsonProcessingException {
		// JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(news);
		LoggerUtil.log("json entered value : " + news);

		// 현재 날짜와 시간 얻기
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String time = now.format(formatter);

		// JSON에서 필요한 데이터 추출
		JsonNode arrayNode = rootNode.get("items");

		ArrayList<ResponseNaverNews> rnn = new ArrayList<ResponseNaverNews>();
		for (JsonNode element : arrayNode) {
			String title = element.get("title").asText(); // 뉴스 제목
			String originallink = element.get("originallink").asText(); // 뉴스링크
			String link = element.get("link").asText(); // 뉴스링크
			String description = element.get("description").asText(); // 설명
			String pubDate = element.get("pubDate").asText(); // 뉴스날짜
			ResponseNaverNews pssop = new ResponseNaverNews(title, originallink, link, description, pubDate);
			rnn.add(pssop);
		}

		return rnn;
	}

	public PositiveStock siJsonParse(String data, String stockCode)
			throws JsonMappingException, JsonProcessingException {

		// Gson 객체 생성
		Gson gson = new Gson();

		// JSON 파싱하여 필요한 부분만 추출
		JsonElement jsonObject = JsonParser.parseString(data).getAsJsonObject().get("output");

			// "output" → "prdt_abrv_name" 내부 JSON 추출
			String stockName = jsonObject.getAsJsonObject().get("prdt_abrv_name").getAsString();

			PositiveStock ps = new PositiveStock(stockName, stockCode);
			return ps;

	}

	public PositiveStock tsJsonParse(String data, String stockName, String stockCode)
			throws JsonMappingException, JsonProcessingException {

		// Gson 객체 생성
		Gson gson = new Gson();

		// JSON 파싱하여 필요한 부분만 추출
		String resultCode = JsonParser.parseString(data).getAsJsonObject().get("rt_cd").getAsString();

		PositiveStock ps = null;
		if (resultCode == "0") {
			ps = new PositiveStock(stockName, stockCode);
		}

		return ps;

	}

	public String aiJsonParse(String data) throws JsonMappingException, JsonProcessingException {
		// Gson 객체 생성
		Gson gson = new Gson();

		// JSON 파싱하여 필요한 부분만 추출
		JsonElement jsonObject = JsonParser.parseString(data).getAsJsonObject().get("Output2");

		// "output" → "prdt_abrv_name" 내부 JSON 추출
		String accountDeposit = jsonObject.getAsJsonObject().get("dncl_amt").getAsString();

		return accountDeposit;
	}

	public ArrayList<SellStockDTO> sbiJsonParse(String data) throws JsonMappingException, JsonProcessingException {
		// Gson 객체 생성
		Gson gson = new Gson();
		ArrayList<SellStockDTO> alssd = new ArrayList<SellStockDTO>();

		// JSON 파싱하여 필요한 부분만 추출
		JsonElement jsonObjects = JsonParser.parseString(data).getAsJsonObject().get("output1");

		if (jsonObjects.isJsonArray()) {
			JsonArray jsonArray = jsonObjects.getAsJsonArray();
			for (JsonElement element : jsonArray) {
				String stockCode = element.getAsJsonObject().get("pdno").getAsString();
				String stockName = element.getAsJsonObject().get("prdt_name").getAsString();
				String stockPrice = element.getAsJsonObject().get("prpr").getAsString();
				String stockNum = element.getAsJsonObject().get("hldg_qty").getAsString();
				String stockProfit = element.getAsJsonObject().get("evlu_pfls_amt").getAsString();
				SellStockDTO ssd = new SellStockDTO(stockName, stockCode, stockNum, stockPrice);
				alssd.add(ssd);
				System.out.println("종목코드: " + stockCode + ", 종목명: " + stockName + ", 손익: " + stockProfit);
			}
		} else {
			System.out.println("JSON은 배열이 아닙니다.");
		}

		return alssd;
	}

	public EstimatedClosingPrice ecpJsonParse(String response) throws JsonMappingException, JsonProcessingException {

		EstimatedClosingPrice estimatesClosingPrice = new EstimatedClosingPrice("", "", "", null, null);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			estimatesClosingPrice = objectMapper.readValue(response, EstimatedClosingPrice.class);
		} catch (Exception e) {
			e.printStackTrace();
			return estimatesClosingPrice;
		}
		return estimatesClosingPrice;

	}

	// parse the access_token
	public String getAccessToken(String apiResult) throws JsonMappingException, JsonProcessingException {
		// JSON 파싱
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(apiResult);

		// access_token 값 추출
		String accessToken = rootNode.get("access_token").asText();
		System.out.println("access_token : " + accessToken);
		return accessToken; // Return the access_token
	}
}
