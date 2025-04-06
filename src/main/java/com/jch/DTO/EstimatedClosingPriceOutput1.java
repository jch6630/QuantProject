package com.jch.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EstimatedClosingPriceOutput1 {

	@JsonProperty("rprs_mrkt_kor_name")
	private String rprsMrktKorName;
	@JsonProperty("antc_cnpr")
	private String antcCnpr;
	@JsonProperty("antc_cntg_vrss_sign")
	private String antcCntgVrssSign;
	@JsonProperty("antc_cntg_vrss")
	private String antcCntgVrss;
	@JsonProperty("antc_cntg_prdy_ctrt")
	private String antcCntgPrdyCtrt;
	@JsonProperty("antc_vol")
	private String antcVol;
	@JsonProperty("antc_tr_pbmn")
	private String antcTrPbmn;

	public String getRprsMrktKorName() {
		return rprsMrktKorName;
	}

	public void setRprsMrktKorName(String rprsMrktKorName) {
		this.rprsMrktKorName = rprsMrktKorName;
	}

	public String getAntcCnpr() {
		return antcCnpr;
	}

	public void setAntcCnpr(String antcCnpr) {
		this.antcCnpr = antcCnpr;
	}

	public String getAntcCntgVrssSign() {
		return antcCntgVrssSign;
	}

	public void setAntcCntgVrssSign(String antcCntgVrssSign) {
		this.antcCntgVrssSign = antcCntgVrssSign;
	}

	public String getAntcCntgVrss() {
		return antcCntgVrss;
	}

	public void setAntcCntgVrss(String antcCntgVrss) {
		this.antcCntgVrss = antcCntgVrss;
	}

	public String getAntcCntgPrdyCtrt() {
		return antcCntgPrdyCtrt;
	}

	public void setAntcCntgPrdyCtrt(String antcCntgPrdyCtrt) {
		this.antcCntgPrdyCtrt = antcCntgPrdyCtrt;
	}

	public String getAntcVol() {
		return antcVol;
	}

	public void setAntcVol(String antcVol) {
		this.antcVol = antcVol;
	}

	public String getAntcTrPbmn() {
		return antcTrPbmn;
	}

	public void setAntcTrPbmn(String antcTrPbmn) {
		this.antcTrPbmn = antcTrPbmn;
	}

	public EstimatedClosingPriceOutput1(String rprsMrktKorName, String antcCnpr, String antcCntgVrssSign,
			String antcCntgVrss, String antcCntgPrdyCtrt, String antcVol, String antcTrPbmn) {
		super();
		this.rprsMrktKorName = rprsMrktKorName;
		this.antcCnpr = antcCnpr;
		this.antcCntgVrssSign = antcCntgVrssSign;
		this.antcCntgVrss = antcCntgVrss;
		this.antcCntgPrdyCtrt = antcCntgPrdyCtrt;
		this.antcVol = antcVol;
		this.antcTrPbmn = antcTrPbmn;
	}

	public EstimatedClosingPriceOutput1() {

	}

	@Override
	public String toString() {
		return "EstimatesClosingPriceOutput1 [rprsMrktKorName=" + rprsMrktKorName + ", antcCnpr=" + antcCnpr
				+ ", antcCntgVrssSign=" + antcCntgVrssSign + ", antcCntgVrss=" + antcCntgVrss + ", antcCntgPrdyCtrt="
				+ antcCntgPrdyCtrt + ", antcVol=" + antcVol + ", antcTrPbmn=" + antcTrPbmn + "]";
	}
}
