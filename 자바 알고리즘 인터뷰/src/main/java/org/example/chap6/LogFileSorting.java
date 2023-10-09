package org.example.chap6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogFileSorting {

	// 문자로 구성된 로그가 숫자로그보다 앞
	// 숫자 로그는 입력 순서대로 둔다
	public String[] reorderLogFiles(String[] logs) {
		List<String> letterList = new ArrayList<>();
		List<String> digitList = new ArrayList<>();

		for (String log : logs) {
			if (Character.isDigit(log.split(" ")[1].charAt(0))) {
				digitList.add(log);
			} else {
				letterList.add(log);
			}
		}

		// 문자가 동일할 경우 식별자 순으로
		letterList.sort((o1, o2) -> {
			String[] s1x = o1.split(" ", 2);
			String[] s2x = o2.split(" ", 2);

			int compared = s1x[1].compareTo(s2x[1]);

			if (compared == 0) {
				return s1x[0].compareTo(s2x[0]);
			} else {
				return compared;
			}
		});

		letterList.addAll(digitList);

		return letterList.toArray(new String[0]);
	}

}
