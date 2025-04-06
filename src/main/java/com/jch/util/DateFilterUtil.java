package com.jch.util;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateFilterUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

	public static boolean isOlderThanOneDays(String dateString) {
        try {
            // 문자열을 LocalDateTime으로 변환
            LocalDateTime givenDate = LocalDateTime.parse(dateString, formatter);

            // 현재 날짜
            LocalDateTime currentDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

            // 두 날짜 간의 차이 계산
            long daysBetween = ChronoUnit.DAYS.between(givenDate, currentDate);

            // 2일 이상 차이 나는 경우 true 반환
			return daysBetween > 1;

        } catch (Exception e) {
            System.err.println("날짜 변환 오류: " + e.getMessage());
            return true; // 오류가 발생하면 안전하게 필터링
        }
    }

}
