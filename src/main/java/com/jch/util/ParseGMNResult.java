package com.jch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseGMNResult {
	public static boolean parseResult(String input) {
		// 정규식을 사용하여 숫자 값 추출
		Pattern pattern = Pattern.compile("\\d"); // 숫자 찾기
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) { // 문자열에서 숫자 검색
			int number = Integer.parseInt(matcher.group());
			if (number == 1) {
				return true;
			} else if (number == 2) {
				return false;
			}
		}

		// 기본 반환값 (숫자가 없거나 정의된 값이 아닐 경우)
		throw new IllegalArgumentException("올바른 숫자가 포함되지 않은 입력: " + input);
	}
}
