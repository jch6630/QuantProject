package com.jch.DTO;

public class KoreaStockApi {

	private String appKey = System.getenv("KSA_APP_KEY"); // API 키
	private String appSecret = System.getenv("KSA_APP_SEC"); // API 비밀 키
	private String ksaUrl = System.getenv("KSA_PUBLIC_URL"); // 공용 URL
	private String ksaTestUrl = System.getenv("KSA_TEST_URL"); // 모의투자 공용 URL
	private String ksaTranUrl = System.getenv("KSA_TRAN_URL"); // 주문 URL
	private String tokenUrl = System.getenv("KSA_TOKEN_URL"); // 토큰 URL
	private String apiNowPriceUrl = System.getenv("KSA_NOW_PRICE_URL"); // 주식현재가 요청 주소
	private String apiChkTopTwnUrl = System.getenv("KSA_CHK_TOP_URL"); // 조회 상위 20 요청 주소
	private String trIdKNP = "FHKST01010100"; // 주식현재가 요청 TR_ID
	private String trIdCTT = "HHMCM000100C0"; // 조회 상위 20 요청 TR_ID
	private String trIdECP = "FHPST01810000"; // 예상체결가 TR_ID
	private String trIdBuy = "TTTC0012U"; // 현금 매수 TR_ID
	private String trIdSell = "TTTC0011U"; // 현금 매도 TR_ID

	public String getKsaTestUrl() {
		return ksaTestUrl;
	}

	public void setKsaTestUrl(String ksaTestUrl) {
		this.ksaTestUrl = ksaTestUrl;
	}

	public String getKsaTranUrl() {
		return ksaTranUrl;
	}

	public void setKsaTranUrl(String ksaTranUrl) {
		this.ksaTranUrl = ksaTranUrl;
	}

	public String getTrIdBuy() {
		return trIdBuy;
	}

	public void setTrIdBuy(String trIdBuy) {
		this.trIdBuy = trIdBuy;
	}

	public String getTrIdSell() {
		return trIdSell;
	}

	public void setTrIdSell(String trIdSell) {
		this.trIdSell = trIdSell;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getKsaUrl() {
		return ksaUrl;
	}

	public void setKsaUrl(String ksaUrl) {
		this.ksaUrl = ksaUrl;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getApiNowPriceUrl() {
		return apiNowPriceUrl;
	}

	public void setApiNowPriceUrl(String apiNowPriceUrl) {
		this.apiNowPriceUrl = apiNowPriceUrl;
	}

	public String getApiChkTopTwnUrl() {
		return apiChkTopTwnUrl;
	}

	public void setApiChkTopTwnUrl(String apiChkTopTwnUrl) {
		this.apiChkTopTwnUrl = apiChkTopTwnUrl;
	}

	public String getTrIdKNP() {
		return trIdKNP;
	}

	public void setTrIdKNP(String trIdKNP) {
		this.trIdKNP = trIdKNP;
	}

	public String getTrIdCTT() {
		return trIdCTT;
	}

	public void setTrIdCTT(String trIdCTT) {
		this.trIdCTT = trIdCTT;
	}

	public String getTrIdECP() {
		return trIdECP;
	}

	public void setTrIdECP(String trIdECP) {
		this.trIdECP = trIdECP;
	}


}
