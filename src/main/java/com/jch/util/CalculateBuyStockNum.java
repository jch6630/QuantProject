package com.jch.util;

import java.util.ArrayList;

import com.jch.request.RequestAccountInfor;

public class CalculateBuyStockNum {
	public ArrayList<String> Calculate(ArrayList<String> stockPrices, boolean trChk) {

		RequestAccountInfor rai = new RequestAccountInfor();
		int accountDeposit = Integer.parseInt(rai.request(trChk)); // 예수금

		ArrayList<String> result = new ArrayList<String>();
		ArrayList<Integer> ratios = new ArrayList<>();
		ArrayList<Integer> mTQRBuyPrices = new ArrayList<>();
		ArrayList<Integer> accountDepositStockPriceRatios = new ArrayList<>(); // 종목별 최대 매매 수량


//		주식현재가 비율
		int accountDepositStockPriceRatiosTotal = 0; // 최대 매매 수량 총합
		for (String stockPrice : stockPrices) {
			int accountStockPriceRatios = accountDeposit / Integer.parseInt(stockPrice);
			accountDepositStockPriceRatios.add(accountStockPriceRatios);
			accountDepositStockPriceRatiosTotal = accountDepositStockPriceRatiosTotal + accountStockPriceRatios;
		}

		for (Integer aDSPR : accountDepositStockPriceRatios) {
			double mTQR = aDSPR / accountDepositStockPriceRatiosTotal; // 최대 매매 수량 비율
			int mTQRBuyPrice = (int) (accountDeposit * mTQR); // 비율별 매매 금액
			mTQRBuyPrices.add(mTQRBuyPrice);
		}

		int stockNum = 0;
		for (String stockPrice : stockPrices) {
			int buyNum = mTQRBuyPrices.get(stockNum) / Integer.parseInt(stockPrice);
			stockNum++;
			String buyNumStr = String.valueOf(buyNum);
			result.add(buyNumStr);
		}

		return result;
	}
}
