package com.jch.DTO;

import java.util.ArrayList;

public class PSSResponseBody {
	private String rt_cd; // 성공 실패 여부
	private String msg_cd; // 응답코드
	private String msg1; // 응답메세지
	private ArrayList<PSSOP> output1; // 응답상세

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public String getRt_cd() {
		return rt_cd;
	}

	public void setRt_cd(String rt_cd) {
		this.rt_cd = rt_cd;
	}

	public String getMsg_cd() {
		return msg_cd;
	}

	public void setMsg_cd(String msg_cd) {
		this.msg_cd = msg_cd;
	}

	public String getMsg1() {
		return msg1;
	}

	public void setMsg1(String msg1) {
		this.msg1 = msg1;
	}

	public ArrayList<PSSOP> getOutput1() {
		return output1;
	}

	public void setOutput1(ArrayList<PSSOP> output1) {
		this.output1 = output1;
	}

	public PSSResponseBody(String rt_cd, String msg_cd, String msg1, ArrayList<PSSOP> output1) {
		super();
		this.rt_cd = rt_cd;
		this.msg_cd = msg_cd;
		this.msg1 = msg1;
		this.output1 = output1;
	}
}
