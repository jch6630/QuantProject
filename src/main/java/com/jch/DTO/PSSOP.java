package com.jch.DTO;

public class PSSOP {
	private String mrktDivClsCode; // 시장구분
	private String mkscShrnIscd; // 종목코드

	public String getMrktDivClsCode() {
		return mrktDivClsCode;
	}

	public void setMrktDivClsCode(String mrktDivClsCode) {
		this.mrktDivClsCode = mrktDivClsCode;
	}

	public String getMkscShrnIscd() {
		return mkscShrnIscd;
	}

	public void setMkscShrnIscd(String mkscShrnIscd) {
		this.mkscShrnIscd = mkscShrnIscd;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public PSSOP(String mrktDivClsCode, String mkscShrnIscd) {
		super();
		this.mrktDivClsCode = mrktDivClsCode;
		this.mkscShrnIscd = mkscShrnIscd;
	}

}
