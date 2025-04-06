package com.jch.util;

import java.time.LocalTime;

public class ScheduleUtill {
	public static long getDelayUntil(int targetHour, int targetMinute) {
		LocalTime now = LocalTime.now();
		LocalTime targetTime = LocalTime.of(targetHour, targetMinute);

		long delay = java.time.Duration.between(now, targetTime).toMinutes();
		return delay < 0 ? delay + 1440 : delay; // 다음 날로 보정
	}
}
