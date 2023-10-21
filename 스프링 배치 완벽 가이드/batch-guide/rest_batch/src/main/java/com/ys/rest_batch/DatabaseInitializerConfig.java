package com.ys.rest_batch;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

// @Configuration
public class DatabaseInitializerConfig {

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource, DataSourceProperties properties, JdbcTemplate jdbcTemplate) {
		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);

		// 데이터베이스에 데이터가 없는 경우만 스키마 스크립트를 실행
		if (isDatabaseEmpty(jdbcTemplate)) {
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.addScript(new ClassPathResource("schema-mysql.sql"));
			initializer.setDatabasePopulator(populator);
		}

		return initializer;
	}

	private boolean isDatabaseEmpty(JdbcTemplate jdbcTemplate) {
		// 아래 쿼리는 예제이며, 실제 데이터베이스 구조에 맞게 수정해야 합니다.
		// 특정 테이블에 레코드가 있는지 확인하는 쿼리로 가정
		Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ACCOUNT_SUMMARY", Integer.class);
		return (count == null || count == 0);
	}

}