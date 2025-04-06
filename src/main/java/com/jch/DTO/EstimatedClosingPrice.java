package com.jch.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EstimatedClosingPrice {

	@JsonProperty("output1")
	private EstimatedClosingPriceOutput1 output1;
	@JsonProperty("output2")
	private List<EstimatedClosingPriceOutput2> output2;
	@JsonProperty("rt_cd")
	private String rtCd;
	@JsonProperty("msg_cd")
	private String msgCd;
	@JsonProperty("msg1")
	private String msg1;

	@Override
	public String toString() {
		return "EstimatesClosingPrice [rtCd=" + rtCd + ", msgCd=" + msgCd + ", msg1=" + msg1 + ", output1=" + output1
				+ ", output2=" + output2 + "]";
	}

	public String getRtCd() {
		return rtCd;
	}

	public void setRtCd(String rtCd) {
		this.rtCd = rtCd;
	}

	public String getMsgCd() {
		return msgCd;
	}

	public void setMsgCd(String msgCd) {
		this.msgCd = msgCd;
	}

	public String getMsg1() {
		return msg1;
	}

	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}

	public EstimatedClosingPriceOutput1 getOutput1() {
		return output1;
	}

	public void setOutput1(EstimatedClosingPriceOutput1 output1) {
		this.output1 = output1;
	}

	public List<EstimatedClosingPriceOutput2> getOutput2() {
		return output2;
	}

	public void setOutput2(List<EstimatedClosingPriceOutput2> output2) {
		this.output2 = output2;
	}

	public EstimatedClosingPrice(String rtCd, String msgCd, String msg1, EstimatedClosingPriceOutput1 output1,
			List<EstimatedClosingPriceOutput2> output2) {
		super();
		this.rtCd = rtCd;
		this.msgCd = msgCd;
		this.msg1 = msg1;
		this.output1 = output1;
		this.output2 = output2;
	}

	public EstimatedClosingPrice() {

	}

}
