package com.jch.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EstimatedClosingPriceOutput2 {

	@JsonProperty("stck_bsop_date")
	private String stckBsopDate;
	@JsonProperty("stck_cntg_hour")
	private String stckCntgHour;
	@JsonProperty("stck_prpr")
	private String stckPrpr;
	@JsonProperty("prdy_vrss_sign")
	private String prdyVrssSign;
	@JsonProperty("prdy_vrss")
	private String prdyVrss;
	@JsonProperty("prdy_ctrt")
	private String prdyCtrt;
	@JsonProperty("acml_vol")
	private String acmlVol;


	public String getStckBsopDate() {
		return stckBsopDate;
	}

	public void setStckBsopDate(String stckBsopDate) {
		this.stckBsopDate = stckBsopDate;
	}

	public String getStckCntgHour() {
		return stckCntgHour;
	}

	public void setStckCntgHour(String stckCntgHour) {
		this.stckCntgHour = stckCntgHour;
	}

	public String getStckPrpr() {
		return stckPrpr;
	}

	public void setStckPrpr(String stckPrpr) {
		this.stckPrpr = stckPrpr;
	}

	public String getPrdyVrssSign() {
		return prdyVrssSign;
	}

	public void setPrdyVrssSign(String prdyVrssSign) {
		this.prdyVrssSign = prdyVrssSign;
	}

	public String getPrdyVrss() {
		return prdyVrss;
	}

	public void setPrdyVrss(String prdyVrss) {
		this.prdyVrss = prdyVrss;
	}

	public String getPrdyCtrt() {
		return prdyCtrt;
	}

	public void setPrdyCtrt(String prdyCtrt) {
		this.prdyCtrt = prdyCtrt;
	}

	public String getAcmlVol() {
		return acmlVol;
	}

	public void setAcmlVol(String acmlVol) {
		this.acmlVol = acmlVol;
	}

	public EstimatedClosingPriceOutput2(String stckBsopDate, String stckCntgHour, String stckPrpr, String prdyVrssSign,
			String prdyVrss, String prdyCtrt, String acmlVol) {
		super();
		this.stckBsopDate = stckBsopDate;
		this.stckCntgHour = stckCntgHour;
		this.stckPrpr = stckPrpr;
		this.prdyVrssSign = prdyVrssSign;
		this.prdyVrss = prdyVrss;
		this.prdyCtrt = prdyCtrt;
		this.acmlVol = acmlVol;
	}

	public EstimatedClosingPriceOutput2() {

	}

	@Override
	public String toString() {
		return "EstimatesClosingPriceOutput2 [stckBsopDate=" + stckBsopDate + ", stckCntgHour=" + stckCntgHour
				+ ", stckPrpr=" + stckPrpr + ", prdyVrssSign=" + prdyVrssSign + ", prdyVrss=" + prdyVrss + ", prdyCtrt="
				+ prdyCtrt + ", acmlVol=" + acmlVol + "]";
	}
}
