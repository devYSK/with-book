package com.ys.book;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SomeTest {

	private final SomeService service = new SomeService();

	@Sql(scripts = {"classpath:sql/age_function.sql"},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@Sql(scripts = {"classpath:sql/drop_age_function.sql"},
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@Test
	void test() {
		int age = 10;
		service.logic("bob", age);

	}

}
