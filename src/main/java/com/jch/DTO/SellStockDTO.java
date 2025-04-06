package com.jch.DTO;

public class SellStockDTO {

	private String Com; // 기업명
	private String StockCode; // 주식코드
	private String StockNum; // 주식코드
	private String StockPrice; // 주식코드

	@Override
	public String toString() {
		return "SellStock [Com=" + Com + ", StockCode=" + StockCode + ", StockNum=" + StockNum + ", StockPrice="
				+ StockPrice + "]";
	}

	public String getCom() {
		return Com;
	}

	public void setCom(String com) {
		Com = com;
	}

	public String getStockCode() {
		return StockCode;
	}

	public void setStockCode(String stockCode) {
		StockCode = stockCode;
	}

	public String getStockNum() {
		return StockNum;
	}

	public void setStockNum(String stockNum) {
		StockNum = stockNum;
	}

	public String getStockPrice() {
		return StockPrice;
	}

	public void setStockPrice(String stockPrice) {
		StockPrice = stockPrice;
	}


	public SellStockDTO(String com, String stockCode, String stockNum, String stockPrice) {
		super();
		Com = com;
		StockCode = stockCode;
		StockNum = stockNum;
		StockPrice = stockPrice;
	}


}
