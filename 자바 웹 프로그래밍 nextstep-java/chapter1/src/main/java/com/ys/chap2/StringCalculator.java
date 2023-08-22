package com.ys.chap2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	public int add(String text) {
		if (isBlank(text)) {
			return 0;
		}

		return sum(toInts(split(text)));
	}

	private String[] split(String text) {
		Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

		if (m.find()) {
			String customDelimeter = m.group(1);
			return m.group(2).split(customDelimeter);
		}

		return text.split(",|:");
	}

	private boolean isBlank(String text) {
		return text == null || text.isEmpty();
	}

	private int[] toInts(String[] values) {
		int[] numbers = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			numbers[i] = toPositive(values, i);
		}
		return numbers;
	}

	private int toPositive(String[] values, int i) {
		int number = Integer.parseInt(values[i]);
		if (number < 0) {
			throw new RuntimeException();
		}
		return number;
	}

	private int sum(int[] numbers) {
		int sum = 0;
		for (int number : numbers) {
			sum += number;
		}
		return sum;
	}

}
