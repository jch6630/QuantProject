package com.jch.DTO;

public class PositiveStock {

	private String Com; // 기업명
	private String StockCode; // 주식코드
    
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

	public PositiveStock(String com, String stockCode) {
		super();
		Com = com;
		StockCode = stockCode;
	}
}
