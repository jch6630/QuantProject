package com.jch.work;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jch.DTO.PSSOP;
import com.jch.DTO.PositiveStock;
import com.jch.DTO.ResponseNaverNews;
import com.jch.DTO.StockData;
import com.jch.manager.DatabaseManager;
import com.jch.request.RequestCheckTopTwenty;
import com.jch.request.RequestNaverNewsApi;
import com.jch.request.RequestNowStockPrice;
import com.jch.request.RequestStockInformation;
import com.jch.util.CompareUtil;
import com.jch.util.DateFilterUtil;
import com.jch.util.JsonParse;
import com.jch.util.LoggerUtil;
import com.jch.util.NewsAnalyze;
import com.jch.util.ParseGMNResult;

public class BuyStockFromNewsAnalysis {

	@SuppressWarnings("static-access")
	public ArrayList<String> DoWork() {
		ArrayList<String> resultArray = new ArrayList<String>();
		
		JsonParse JP = new JsonParse();
		ParseGMNResult pgmnr = new ParseGMNResult();

		RequestNowStockPrice rnsp = new RequestNowStockPrice(); // 주식 현재가
		RequestNaverNewsApi rnna = new RequestNaverNewsApi(); // 네이버 뉴스 받아오기
		
		NewsAnalyze na = new NewsAnalyze(); // 뉴스 분석
		CompareUtil cu = new CompareUtil();

		DatabaseManager dbManager = new DatabaseManager();

		String requestLogId = UUID.randomUUID().toString(); // 각 요청마다 고유한 ID 생성

		RequestCheckTopTwenty rctt = new RequestCheckTopTwenty(); // 조회수 상위 20 항목

		try {

			int failCnt = 0;

			ArrayList<PositiveStock> stockImformations = new ArrayList<PositiveStock>();
			ArrayList<StockData> sdArray = new ArrayList<StockData>();
			RequestStockInformation rsi = new RequestStockInformation();
			DateFilterUtil dfu = new DateFilterUtil();
			ArrayList<String> newsAnalyresultArray = new ArrayList<String>();

			for (PSSOP pssunit : JP.PSSJsonParse(rctt.request()).getOutput1()) {

				StockData sd = new StockData();

				String marketCode = pssunit.getMrktDivClsCode();
				String stockCode = pssunit.getMkscShrnIscd();

				stockImformations.add(rsi.request(stockCode));

				LoggerUtil.logInfo("주식 조회 상위 목록 파싱 성공! 시장구분 : " + marketCode + ", 종목코드 : " + stockCode, requestLogId);
			}

			for (PositiveStock positiveStock : stockImformations) {

				String comName = positiveStock.getCom();
				String comStockCode = positiveStock.getStockCode();

				try {
					String news = rnna.searchNews(comName);
					LoggerUtil.logInfo("네이버 뉴스 받아오기 성공! : " + news, requestLogId);

					ArrayList<String> newsLinks = new ArrayList<String>(); // 기업과 관련된 뉴스 묶음

					for (ResponseNaverNews naverNews : JP.nnJsonParse(news)) {
						Thread.sleep(200);
						if (DateFilterUtil.isOlderThanOneDays(naverNews.getPubDate())) {
							// 이틀보다 오래된 뉴스 처리
							continue;
						} else {
							if (naverNews.getOriginallink().length() == 0) {
								continue;
							} else {
								newsLinks.add(" - ");
								newsLinks.add(naverNews.getOriginallink());
							}
						}
					}

					if (newsLinks.isEmpty()) {
						continue;
					} else {

						String geminiresultArray[] = na.newsAnalyze(comName, comStockCode, newsLinks); // gemini가 뉴스 분석한
																										// 결과

						newsAnalyresultArray.add(geminiresultArray[0]);
						newsAnalyresultArray.add(geminiresultArray[1]);
						newsAnalyresultArray.add(geminiresultArray[2]);

						if (geminiresultArray[2].equals("Failed")) { // 분석 실패 시 로그 송출과 실패 횟수 카운트
							LoggerUtil.logInfo("News analyze is Failed! : ", requestLogId);
							failCnt++;
						} else {
							if (pgmnr.parseResult(geminiresultArray[2])) {
								String nowStockInform = rnsp.request(geminiresultArray[1]);

								// JSON 파싱
								ObjectMapper objectMapper = new ObjectMapper();
								JsonNode output = objectMapper.readTree(nowStockInform).path("output");

								String iscdStatClsCode = output.path("iscd_stat_cls_code").asText();
								String margRate = output.path("marg_rate").asText();
								String rprsMrktKorName = output.path("rprs_mrkt_kor_name").asText();
								String newHgprLwprClsCode = output.path("new_hgpr_lwpr_cls_code").asText();
								String bstpKorIsnm = output.path("bstp_kor_isnm").asText();
								String tempStopYn = output.path("temp_stop_yn").asText();
								String oprcRangContYn = output.path("oprc_rang_cont_yn").asText();
								String clprRangContYn = output.path("clpr_rang_cont_yn").asText();
								String crdtAbleYn = output.path("crdt_able_yn").asText();
								String grmnRateClsCode = output.path("grmn_rate_cls_code").asText();
								String elwPblcYn = output.path("elw_pblc_yn").asText();
								String stckPrpr = output.path("stck_prpr").asText(); // 현재주식가
								String prdyVrss = output.path("prdy_vrss").asText();
								String prdyVrssSign = output.path("prdy_vrss_sign").asText();
								String prdyCtrt = output.path("prdy_ctrt").asText();
								String acmlTrPbmn = output.path("acml_tr_pbmn").asText();
								String acmlVol = output.path("acml_vol").asText();
								String prdyVrssVolRate = output.path("prdy_vrss_vol_rate").asText();
								String stckOprc = output.path("stck_oprc").asText();
								String stckHgpr = output.path("stck_hgpr").asText();
								String stckLwpr = output.path("stck_lwpr").asText();
								String stckMxpr = output.path("stck_mxpr").asText();
								String stckLlam = output.path("stck_llam").asText();
								String stckSdpr = output.path("stck_sdpr").asText();
								String wghnAvrgStckPrc = output.path("wghn_avrg_stck_prc").asText();
								String htsFrgnEhrt = output.path("hts_frgn_ehrt").asText();
								String frgnNtbyQty = output.path("frgn_ntby_qty").asText();
								String pgtrNtbyQty = output.path("pgtr_ntby_qty").asText();
								String pvtScndDmrsPrc = output.path("pvt_scnd_dmrs_prc").asText();
								String pvtFrstDmrsPrc = output.path("pvt_frst_dmrs_prc").asText();
								String pvtPontVal = output.path("pvt_pont_val").asText();
								String pvtFrstDmspPrc = output.path("pvt_frst_dmsp_prc").asText();
								String pvtScndDmspPrc = output.path("pvt_scnd_dmsp_prc").asText();
								String dmrsVal = output.path("dmrs_val").asText();
								String dmspVal = output.path("dmsp_val").asText();
								String cpfn = output.path("cpfn").asText();
								String rstcWdthPrc = output.path("rstc_wdth_prc").asText();
								String stckFcam = output.path("stck_fcam").asText();
								String stckSspr = output.path("stck_sspr").asText();
								String asprUnit = output.path("aspr_unit").asText();
								String htsDealQtyUnitVal = output.path("hts_deal_qty_unit_val").asText();
								String lstnStcn = output.path("lstn_stcn").asText();
								String htsAvls = output.path("hts_avls").asText();
								String per = output.path("per").asText();
								String pbr = output.path("pbr").asText();
								String stacMonth = output.path("stac_month").asText();
								String volTnrt = output.path("vol_tnrt").asText();
								String eps = output.path("eps").asText();
								String bps = output.path("bps").asText();
								String d250Hgpr = output.path("d250_hgpr").asText();
								String d250HgprDate = output.path("d250_hgpr_date").asText();
								String d250HgprVrssPrprRate = output.path("d250_hgpr_vrss_prpr_rate").asText();
								String d250Lwpr = output.path("d250_lwpr").asText();
								String d250LwprDate = output.path("d250_lwpr_date").asText();
								String d250LwprVrssPrprRate = output.path("d250_lwpr_vrss_prpr_rate").asText();
								String stckDryyHgpr = output.path("stck_dryy_hgpr").asText();
								String dryyHgprVrssPrprRate = output.path("dryy_hgpr_vrss_prpr_rate").asText();
								String dryyHgprDate = output.path("dryy_hgpr_date").asText();
								String stckDryyLwpr = output.path("stck_dryy_lwpr").asText();
								String dryyLwprVrssPrprRate = output.path("dryy_lwpr_vrss_prpr_rate").asText();
								String dryyLwprDate = output.path("dryy_lwpr_date").asText();
								String w52Hgpr = output.path("w52_hgpr").asText();
								String w52HgprVrssPrprCtrt = output.path("w52_hgpr_vrss_prpr_ctrt").asText();
								String w52HgprDate = output.path("w52_hgpr_date").asText();
								String w52Lwpr = output.path("w52_lwpr").asText();
								String w52LwprVrssPrprCtrt = output.path("w52_lwpr_vrss_prpr_ctrt").asText();
								String w52LwprDate = output.path("w52_lwpr_date").asText();
								String wholLoanRmndRate = output.path("whol_loan_rmnd_rate").asText();
								String sstsYn = output.path("ssts_yn").asText();
								String stckShrnIscd = output.path("stck_shrn_iscd").asText();
								String fcamCnnm = output.path("fcam_cnnm").asText();
								String cpfnCnnm = output.path("cpfn_cnnm").asText();
								String apprchRate = output.path("apprch_rate").asText();
								String frgnHldnQty = output.path("frgn_hldn_qty").asText();
								String viClsCode = output.path("vi_cls_code").asText();
								String ovtmViClsCode = output.path("ovtm_vi_cls_code").asText();
								String lastSstsCntgQty = output.path("last_ssts_cntg_qty").asText();
								String invtCafulYn = output.path("invt_caful_yn").asText();
								String mrktWarnClsCode = output.path("mrkt_warn_cls_code").asText();
								String shortOverYn = output.path("short_over_yn").asText();
								String sltrYn = output.path("sltr_yn").asText();

								StockData stock = new StockData(geminiresultArray[0], geminiresultArray[1],
										iscdStatClsCode,
										margRate, rprsMrktKorName, newHgprLwprClsCode, bstpKorIsnm, tempStopYn,
										oprcRangContYn, clprRangContYn, crdtAbleYn, grmnRateClsCode, elwPblcYn,
										stckPrpr, prdyVrss, prdyVrssSign, prdyCtrt, acmlTrPbmn, acmlVol,
										prdyVrssVolRate, stckOprc, stckHgpr, stckLwpr, stckMxpr, stckLlam, stckSdpr,
										wghnAvrgStckPrc, htsFrgnEhrt, frgnNtbyQty, pgtrNtbyQty, pvtScndDmrsPrc,
										pvtFrstDmrsPrc, pvtPontVal, pvtFrstDmspPrc, pvtScndDmspPrc, dmrsVal, dmspVal,
										cpfn, rstcWdthPrc, stckFcam, stckSspr, asprUnit, htsDealQtyUnitVal, lstnStcn,
										htsAvls, per, pbr, stacMonth, volTnrt, eps, bps, d250Hgpr, d250HgprDate,
										d250HgprVrssPrprRate, d250Lwpr, d250LwprDate, d250LwprVrssPrprRate,
										stckDryyHgpr, dryyHgprVrssPrprRate, dryyHgprDate, stckDryyLwpr,
										dryyLwprVrssPrprRate, dryyLwprDate, w52Hgpr, w52HgprVrssPrprCtrt, w52HgprDate,
										w52Lwpr, w52LwprVrssPrprCtrt, w52LwprDate, wholLoanRmndRate, sstsYn,
										stckShrnIscd, fcamCnnm, cpfnCnnm, apprchRate, frgnHldnQty, viClsCode,
										ovtmViClsCode, lastSstsCntgQty, invtCafulYn, mrktWarnClsCode, shortOverYn,
										sltrYn);


								// 예상 체결가
								boolean estimatedClosingPrice = cu.CheckEstimatedClosingPrice(comStockCode, stock);

								String resultArrayCompareUtil = "";

								if (estimatedClosingPrice) {
									dbManager.insertStockData(stock);
									resultArrayCompareUtil = "Expected closing price is good.";
									resultArray.add("회사명 : " + geminiresultArray[0]);
									resultArray.add("주식코드 : " + geminiresultArray[1]);
									resultArray.add("현재주식가 : " + stock.getStckPrpr() + "/");
								} else {
									System.out.println(
											"com : " + geminiresultArray[0] + ", stockCode : " + geminiresultArray[1]
											+ ", Stock present price below expected closing price.");
									resultArrayCompareUtil = "Expected closing price is bad.";
								}

								newsAnalyresultArray.add(resultArrayCompareUtil);

							} else {
								System.out.println(
										"com : " + geminiresultArray[0] + ", stockCode : " + geminiresultArray[1]
										+ ", gmnRsult is  negative.");
							}
						}
					}

				} catch (Exception e) {
					LoggerUtil.logSevere("네이버 뉴스 받아오는 중 오류 발생 : " + e.getMessage(), requestLogId);
					e.printStackTrace();
				}
			}

			LoggerUtil.logInfo("Number of news analysis failures : " + failCnt, requestLogId);

			resultArray.add("뉴스 분석 실패 횟수 : " + failCnt);

		} catch (JsonProcessingException e) {
			LoggerUtil.logSevere("주식 조회 상위 목록 파싱 중 오류 발생 : " + e.getMessage(), requestLogId);
			e.printStackTrace();
		} finally {
			dbManager.close();
		}
		return resultArray;
	}
}
