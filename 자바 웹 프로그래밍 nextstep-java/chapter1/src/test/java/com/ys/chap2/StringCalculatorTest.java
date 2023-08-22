package com.ys.chap2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringCalculatorTest {

	private StringCalculator cal;

	@BeforeEach
	void init() {
		cal = new StringCalculator();
	}

	@DisplayName("null 또는 빈 문자")
	@Test
	void add_null_empty() {
		assertEquals(0, cal.add(null));
		assertEquals(0, cal.add(""));
	}

	@DisplayName("숫자 하나")
	@Test
	void add_number1() throws Exception {
		assertEquals(1, cal.add("1"));
	}

	@DisplayName("쉼표 구분자")
	@Test
	void add_comma() throws Exception {
		assertEquals(3, cal.add("1,2"));
	}

	@DisplayName("쉼표 또는 콜론 구분자")
	@Test
	void add_comma_separator() throws Exception {
		assertEquals(6, cal.add("1,2:3"));
	}

	@DisplayName("커스텀 구분자")
	@Test
	void add_custom_separator() throws Exception {
		assertEquals(6, cal.add("//;\n1;2;3"));
	}

	@DisplayName("음수 전달시 예외를 던진다")
	@Test
	void add_negative() throws Exception {
		assertThrows(RuntimeException.class,
			() -> cal.add("-1,2,3"));
	}

}