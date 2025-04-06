package com.jch;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jch.DTO.SellStockDTO;
import com.jch.request.RequestNowStockPrice;
import com.jch.request.RequestStockBalanceInquiry;
import com.jch.sender.KakaoMessageSender;
import com.jch.transaction.BuyStock;
import com.jch.transaction.SellStock;
import com.jch.util.CalculateBuyStockNum;
import com.jch.util.JsonParse;
import com.jch.util.KakaoTokenUtil;
import com.jch.util.ScheduleUtill;
import com.jch.work.BuyStockFromNewsAnalysis;

public class Main {
	private static final String LOG_ID = UUID.randomUUID().toString();
	private static final Logger logger = Logger.getLogger(RequestNowStockPrice.class.getName());
	private static final boolean TR_CHK = false; // true: 모의투자, false: 실투자
	private static final boolean Run_Check = true; // true: 스케줄 실행, false: 직접 실행

	public static void main(String[] args) {
		LocalDate today = LocalDate.now();
		DayOfWeek day = today.getDayOfWeek();

		if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
			System.out.println("주말입니다. 프로그램을 종료합니다.");
			return;
		}
		if (Run_Check) {
			System.out.println("스케줄 실행 모드입니다.");
			scheduleTasks(); // 스케줄러 실행
		} else {
			System.out.println("직접 실행 모드입니다.");
			runDirect(); // 직접 실행
		}
	}

	// 직접 실행 로직
	private static void runDirect() {
		BuyStockFromNewsAnalysis bsfna = new BuyStockFromNewsAnalysis();
		BuyStock bS = new BuyStock();
		SellStock sS = new SellStock();
		CalculateBuyStockNum cBSN = new CalculateBuyStockNum();

		ArrayList<String> workResult = bsfna.DoWork();
		ArrayList<String[]> extractedAll = extractAllStockData(workResult);

		ArrayList<String> prices = new ArrayList<>();
		for (String[] data : extractedAll) {
			System.out.println("추출된 주식 데이터: " + Arrays.toString(data));
			prices.add(data[2]);
		}

		ArrayList<String> buyStockNums = cBSN.Calculate(prices, TR_CHK);
		for (int i = 0; i < extractedAll.size(); i++) {
			String[] data = extractedAll.get(i);
			bS.request(data[0], data[1], data[2], buyStockNums.get(i), TR_CHK);
		}

		sendMessage(workResult.toString());

		RequestStockBalanceInquiry rsbi = new RequestStockBalanceInquiry();
		ArrayList<SellStockDTO> stocksForSell = rsbi.request(TR_CHK);
		for (SellStockDTO stock : stocksForSell) {
			sS.request(stock.getCom(), stock.getStockCode(), stock.getStockNum(), stock.getStockPrice(), TR_CHK);
		}
	}

	// 스케줄러 등록
	private static void scheduleTasks() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		doWork(scheduler, 9, 5); // 매수
		doWork(scheduler, 9, 50); // 매도
	}

	// 스케줄 작업
	private static void doWork(ScheduledExecutorService scheduler, int hour, int minute) {
		long delay = new ScheduleUtill().getDelayUntil(hour, minute);
		if (hour == 9 && minute == 5) {
			scheduler.scheduleAtFixedRate(() -> executeBuyTask(), delay, 1440, TimeUnit.MINUTES);
		} else {
			scheduler.scheduleAtFixedRate(() -> executeSellTask(), delay, 1440, TimeUnit.MINUTES);
		}
	}

	// 매수 작업
	private static void executeBuyTask() {
		System.out.println("[매수 스케줄] 실행 시간: " + LocalTime.now());
		BuyStockFromNewsAnalysis bsfna = new BuyStockFromNewsAnalysis();
		BuyStock bS = new BuyStock();
		CalculateBuyStockNum cBSN = new CalculateBuyStockNum();

		ArrayList<String> workResult = bsfna.DoWork();
		ArrayList<String[]> extractedAll = extractAllStockData(workResult);

		ArrayList<String> prices = new ArrayList<>();
		for (String[] data : extractedAll)
			prices.add(data[2]);
		ArrayList<String> buyStockNums = cBSN.Calculate(prices, TR_CHK);

		for (int i = 0; i < extractedAll.size(); i++) {
			String[] data = extractedAll.get(i);
			bS.request(data[0], data[1], data[2], buyStockNums.get(i), TR_CHK);
		}

		sendMessage(workResult.toString());
	}

	// 매도 작업
	private static void executeSellTask() {
		System.out.println("[매도 스케줄] 실행 시간: " + LocalTime.now());
		RequestStockBalanceInquiry rsbi = new RequestStockBalanceInquiry();
		SellStock sS = new SellStock();

		StringBuilder sellResult = new StringBuilder();
		ArrayList<SellStockDTO> stocksForSell = rsbi.request(TR_CHK);
		for (SellStockDTO stock : stocksForSell) {
			sellResult.append(sS.request(stock.getCom(), stock.getStockCode(), stock.getStockNum(),
					stock.getStockPrice(), TR_CHK)).append("\n");
		}

		sendMessage(sellResult.toString());
	}

	// 주식 데이터 추출
	private static ArrayList<String[]> extractAllStockData(ArrayList<String> workResult) {
		ArrayList<String[]> extractedAll = new ArrayList<>();
		String combined = workResult.toString();
		String[] resultRows = combined.split("뉴스 분석 실패 횟수 : 0\\]");

		for (String row : resultRows) {
			if (row == null || row.trim().isEmpty())
				continue;
			String[] companies = row.split("/");
			for (String company : companies) {
				if (company == null || company.trim().isEmpty())
					continue;
				String[][] extractedData = extractStockData(company);
				for (String[] stock : extractedData)
					extractedAll.add(stock);
			}
		}

		return extractedAll;
	}

	// 정규식 기반 추출
	private static String[][] extractStockData(String input) {
		ArrayList<String[]> resultList = new ArrayList<>();
		Pattern pattern = Pattern.compile("회사명 : (.*?), 주식코드 : (.*?), 현재주식가 : (\\d+)");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			resultList.add(new String[] { matcher.group(1), matcher.group(2), matcher.group(3) });
		}
		return resultList.toArray(new String[0][0]);
	}

	// 메시지 전송
	private static void sendMessage(String message) {
		if (message.length() < 200) {
			sendTalk(message);
		} else {
			for (int i = 0; i < message.length(); i += 190) {
				String part = message.substring(i, Math.min(i + 190, message.length()));
				sendTalk(part);
			}
		}
	}

	private static void sendTalk(String message) {
		try {
			String apiResult = KakaoTokenUtil.getToken();
			String accessToken = new JsonParse().getAccessToken(apiResult);
			KakaoMessageSender.sendMessageToMyself(message, accessToken);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
