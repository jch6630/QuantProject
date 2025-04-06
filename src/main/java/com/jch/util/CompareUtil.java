package com.jch.util;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jch.DTO.EstimatedClosingPrice;
import com.jch.DTO.StockData;
import com.jch.manager.DatabaseManager;
import com.jch.request.RequestEstimatedClosingPrice;

public class CompareUtil {

	JsonParse JP = new JsonParse();

	DatabaseManager dbManager = new DatabaseManager();

	RequestEstimatedClosingPrice recp = new RequestEstimatedClosingPrice(); // 예상체결가

	public boolean CheckEstimatedClosingPrice(String comStockCode, StockData stock) {
		// TODO Auto-generated constructor stub
		EstimatedClosingPrice ecp;
		try {

			ecp = JP.ecpJsonParse(recp.request(comStockCode));
			String antcCnpr = ecp.getOutput1().getAntcCnpr();

			if (Integer.valueOf(antcCnpr) >= Integer.valueOf(stock.getStckPrpr())) {
				int insertEstimatedClosingPrice1 = dbManager.insertEstimatedClosingPrice1(ecp.getOutput1());
				List<Integer> insertEstimatedClosingPrice2 = dbManager.insertEstimatedClosingPrice2(ecp.getOutput2());
				System.out.println(insertEstimatedClosingPrice1 + insertEstimatedClosingPrice2.toString());
				dbManager.insertEstimatedClosingPrice(insertEstimatedClosingPrice1, insertEstimatedClosingPrice2);
				return true;
			} else {
				return false;
			}
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
