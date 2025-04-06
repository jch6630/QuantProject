package com.jch.manager;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jch.DTO.EstimatedClosingPriceOutput1;
import com.jch.DTO.EstimatedClosingPriceOutput2;
import com.jch.DTO.StockData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
	private static final HikariConfig config = new HikariConfig();
	private static HikariDataSource dataSource;

	static {
		try {
//			config.setJdbcUrl("jdbc:mysql://localhost:3306/quantdb?serverTimezone=Asia/Seoul");
			config.setJdbcUrl("jdbc:mariadb://localhost:3306/quantdb");
			config.setUsername("jch6630");
			config.setPassword("9712rhdclf!");
			config.setMaximumPoolSize(10); // 풀의 최대 크기 설정
			config.setConnectionTimeout(30000);
			config.setIdleTimeout(600000);
			config.setMaxLifetime(1800000);

			dataSource = new HikariDataSource(config);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("DatabaseManager 초기화 실패", e);
		}
	}

	// 데이터베이스 연결을 가져오는 메소드
	public static Connection getConnection() throws SQLException {
		if (dataSource == null || dataSource.isClosed()) {
			throw new SQLException("HikariDataSource가 닫혀 있음!");
		}
		return dataSource.getConnection();
	}

	// 데이터베이스에 현재주식가 데이터를 삽입하는 메서드
	public static void insertStockData(StockData stock) {
		String sql = "INSERT INTO stock (COM_NAME, STOCK_CODE, ISCD_STAT_CLS_CODE, MARG_RATE, RPRS_MRKT_KOR_NAME, NEW_HGPR_LWPR_CLS_CODE, BSTP_KOR_ISNM, TEMP_STOP_YN, OPRC_RANG_CONT_YN, CLPR_RANG_CONT_YN, CRDT_ABLE_YN, GRMN_RATE_CLS_CODE, ELW_PBLC_YN, STCK_PRPR, PRDY_VRSS, PRDY_VRSS_SIGN, PRDY_CTRT, ACML_TR_PBMN, ACML_VOL, PRDY_VRSS_VOL_RATE, STCK_OPRC, STCK_HGPR, STCK_LWPR, STCK_MXPR, STCK_LLAM, STCK_SDPR, WGHN_AVRG_STCK_PRC, HTS_FRGN_EHRT, FRGN_NTBY_QTY, PGTR_NTBY_QTY, PVT_SCND_DMRS_PRC, PVT_FRST_DMRS_PRC, PVT_PONT_VAL, PVT_FRST_DMSP_PRC, PVT_SCND_DMSP_PRC, DMRS_VAL, DMSP_VAL, CPFN, RSTC_WDTH_PRC, STCK_FCAM, STCK_SSPR, ASPR_UNIT, HTS_DEAL_QTY_UNIT_VAL, LSTN_STCN, HTS_AVLS, PER, PBR, STAC_MONTH, VOL_TNRT, EPS, BPS, D250_HGPR, D250_HGPR_DATE, D250_HGPR_VRSS_PRPR_RATE, D250_LWPR, D250_LWPR_DATE, D250_LWPR_VRSS_PRPR_RATE, STCK_DRYY_HGPR, DRYY_HGPR_VRSS_PRPR_RATE, DRYY_HGPR_DATE, STCK_DRYY_LWPR, DRYY_LWPR_VRSS_PRPR_RATE, DRYY_LWPR_DATE, W52_HGPR, W52_HGPR_VRSS_PRPR_CTRT, W52_HGPR_DATE, W52_LWPR, W52_LWPR_VRSS_PRPR_CTRT, W52_LWPR_DATE, WHOL_LOAN_RMND_RATE, SSTS_YN, STCK_SHRN_ISCD, FCAM_CNNM, CPFN_CNNM, APPRCH_RATE, FRGN_HLDN_QTY, VI_CLS_CODE, OVTM_VI_CLS_CODE, LAST_SSTS_CNTG_QTY, INVT_CAFUL_YN, MRKT_WARN_CLS_CODE, SHORT_OVER_YN, SLTR_YN) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			// ?에 값 바인딩
			statement.setString(1, stock.getCom());
			statement.setString(2, stock.getStockCode());
			statement.setString(3, stock.getIscdStatClsCode());
			statement.setString(4, stock.getMargRate());
			statement.setString(5, stock.getRprsMrktKorName());
			statement.setString(6, stock.getNewHgprLwprClsCode());
			statement.setString(7, stock.getBstpKorIsnm());
			statement.setString(8, stock.getTempStopYn());
			statement.setString(9, stock.getOprcRangContYn());
			statement.setString(10, stock.getClprRangContYn());
			statement.setString(11, stock.getCrdtAbleYn());
			statement.setString(12, stock.getGrmnRateClsCode());
			statement.setString(13, stock.getElwPblcYn());
			statement.setString(14, stock.getStckPrpr());
			statement.setString(15, stock.getPrdyVrss());
			statement.setString(16, stock.getPrdyVrssSign());
			statement.setString(17, stock.getPrdyCtrt());
			statement.setString(18, stock.getAcmlTrPbmn());
			statement.setString(19, stock.getAcmlVol());
			statement.setString(20, stock.getPrdyVrssVolRate());
			statement.setString(21, stock.getStckOprc());
			statement.setString(22, stock.getStckHgpr());
			statement.setString(23, stock.getStckLwpr());
			statement.setString(24, stock.getStckMxpr());
			statement.setString(25, stock.getStckLlam());
			statement.setString(26, stock.getStckSdpr());
			statement.setString(27, stock.getWghnAvrgStckPrc());
			statement.setString(28, stock.getHtsFrgnEhrt());
			statement.setString(29, stock.getFrgnNtbyQty());
			statement.setString(30, stock.getPgtrNtbyQty());
			statement.setString(31, stock.getPvtScndDmrsPrc());
			statement.setString(32, stock.getPvtFrstDmrsPrc());
			statement.setString(33, stock.getPvtPontVal());
			statement.setString(34, stock.getPvtFrstDmspPrc());
			statement.setString(35, stock.getPvtScndDmspPrc());
			statement.setString(36, stock.getDmrsVal());
			statement.setString(37, stock.getDmspVal());
			statement.setString(38, stock.getCpfn());
			statement.setString(39, stock.getRstcWdthPrc());
			statement.setString(40, stock.getStckFcam());
			statement.setString(41, stock.getStckSspr());
			statement.setString(42, stock.getAsprUnit());
			statement.setString(43, stock.getHtsDealQtyUnitVal());
			statement.setString(44, stock.getLstnStcn());
			statement.setString(45, stock.getHtsAvls());
			statement.setString(46, stock.getPer());
			statement.setString(47, stock.getPbr());
			statement.setString(48, stock.getStacMonth());
			statement.setString(49, stock.getVolTnrt());
			statement.setString(50, stock.getEps());
			statement.setString(51, stock.getBps());
			statement.setString(52, stock.getD250Hgpr());
			statement.setString(53, stock.getD250HgprDate());
			statement.setString(54, stock.getD250HgprVrssPrprRate());
			statement.setString(55, stock.getD250Lwpr());
			statement.setString(56, stock.getD250LwprDate());
			statement.setString(57, stock.getD250LwprVrssPrprRate());
			statement.setString(58, stock.getStckDryyHgpr());
			statement.setString(59, stock.getDryyHgprVrssPrprRate());
			statement.setString(60, stock.getDryyHgprDate());
			statement.setString(61, stock.getStckDryyLwpr());
			statement.setString(62, stock.getDryyLwprVrssPrprRate());
			statement.setString(63, stock.getDryyLwprDate());
			statement.setString(64, stock.getW52Hgpr());
			statement.setString(65, stock.getW52HgprVrssPrprCtrt());
			statement.setString(66, stock.getW52HgprDate());
			statement.setString(67, stock.getW52Lwpr());
			statement.setString(68, stock.getW52LwprVrssPrprCtrt());
			statement.setString(69, stock.getW52LwprDate());
			statement.setString(70, stock.getWholLoanRmndRate());
			statement.setString(71, stock.getSstsYn());
			statement.setString(72, stock.getStckShrnIscd());
			statement.setString(73, stock.getFcamCnnm());
			statement.setString(74, stock.getCpfnCnnm());
			statement.setString(75, stock.getApprchRate());
			statement.setString(76, stock.getFrgnHldnQty());
			statement.setString(77, stock.getViClsCode());
			statement.setString(78, stock.getOvtmViClsCode());
			statement.setString(79, stock.getLastSstsCntgQty());
			statement.setString(80, stock.getInvtCafulYn());
			statement.setString(81, stock.getMrktWarnClsCode());
			statement.setString(82, stock.getShortOverYn());
			statement.setString(83, stock.getSltrYn());

			// 쿼리 실행
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new stock data was inserted successfully!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 데이터베이스에 예상체결가 데이터를 삽입하는 메서드
	public static int insertEstimatedClosingPrice1(EstimatedClosingPriceOutput1 ecp1) {
		String sql = "INSERT INTO estimatedClosingPriceOutput1 (RPRS_MRKT_KOR_NAME, ANTC_CNPR, ANTC_CNTG_VRSS_SIGN, ANTC_CNTG_VRSS, ANTC_CNTG_PRDY_CTRT, ANTC_VOL, ANTC_TR_PBMN) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		int estimatedClosingPriceOutput1ID = -1;

		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			// ?에 값 바인딩
			statement.setString(1, ecp1.getRprsMrktKorName());
			statement.setString(2, ecp1.getAntcCnpr());
			statement.setString(3, ecp1.getAntcCntgVrssSign());
			statement.setString(4, ecp1.getAntcCntgVrss());
			statement.setString(5, ecp1.getAntcCntgPrdyCtrt());
			statement.setString(6, ecp1.getAntcVol());
			statement.setString(7, ecp1.getAntcTrPbmn());

			// 쿼리 실행
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new stock data was inserted successfully!");
			}
			var rs = statement.getGeneratedKeys();
			if (rs.next()) {
				estimatedClosingPriceOutput1ID = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return estimatedClosingPriceOutput1ID;
	}

	// 데이터베이스에 예상체결가(지난 체결가) 데이터를 삽입하는 메서드
	public static List<Integer> insertEstimatedClosingPrice2(List<EstimatedClosingPriceOutput2> list) {
		String sql = "INSERT INTO estimatedClosingPriceOutput2 (STCK_BSOP_DATE, STCK_CNTG_HOUR, STCK_PRPR, PRDY_VRSS_SIGN, PRDY_VRSS, PRDY_CTRT, ACML_VOL) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";


		// 컬럼의 id 반환
		List<Integer> tradeIds = new ArrayList<>();

		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			// ?에 값 바인딩

			for (EstimatedClosingPriceOutput2 ecp2 : list) {
				statement.setString(1, ecp2.getStckBsopDate());
				statement.setString(2, ecp2.getStckCntgHour());
				statement.setString(3, ecp2.getStckPrpr());
				statement.setString(4, ecp2.getPrdyVrssSign());
				statement.setString(5, ecp2.getPrdyVrss());
				statement.setString(6, ecp2.getPrdyCtrt());
				statement.setString(7, ecp2.getAcmlVol());

				statement.addBatch();
			}

			// 쿼리 실행
			statement.executeBatch();

			// 컬럼의 id 반환
			var rs = statement.getGeneratedKeys();
			while (rs.next()) {
				tradeIds.add(rs.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 컬럼의 id 반환
		return tradeIds;
	}

	public static void insertEstimatedClosingPrice(int estimatedClosingPriceOutput1_id, List<Integer> tradeIds) {
		String sql = "INSERT INTO estimatedClosingPrice (EstimatedClosingPriceOutput1_id, EstimatedClosingPriceOutput2_id) VALUES (?, ?)";

		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int estimatedClosingPriceOutput2_id : tradeIds) {
				statement.setInt(1, estimatedClosingPriceOutput1_id);
				statement.setInt(2, estimatedClosingPriceOutput2_id);
				statement.addBatch();
				System.out.println("insertEstimatedClosingPrice : " + String.valueOf(estimatedClosingPriceOutput1_id)
						+ String.valueOf(estimatedClosingPriceOutput2_id));
			}
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}
