/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ys.itemwriter.jdbc;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.ys.itemwriter.domain.Customer;

/**
 * @author Michael Minella
 */
// @Configuration
public class JdbcImportJobConfig {

	private JobBuilderFactory jobBuilderFactory;

	private StepBuilderFactory stepBuilderFactory;

	@Bean
	@StepScope
	public FlatFileItemReader<Customer> customerFileReader(
			@Value("#{jobParameters['customerFile']}") Resource inputFile) {

		return new FlatFileItemReaderBuilder<Customer>()
				.name("customerFileReader")
				.resource(inputFile)
				.delimited()
				.names("firstName",
					"middleInitial",
					"lastName",
					"address",
					"city",
					"state",
					"zip")
				.targetType(Customer.class)
				.build();
	}

//	@Bean
//	public JdbcBatchItemWriter<Customer> jdbcCustomerWriter(DataSource dataSource) throws Exception {
//		return new JdbcBatchItemWriterBuilder<Customer>()
//				.dataSource(dataSource)
//				.sql("INSERT INTO CUSTOMER (first_name, " +
//						"middle_initial, " +
//						"last_name, " +
//						"address, " +
//						"city, " +
//						"state, " +
//						"zip) VALUES (?, ?, ?, ?, ?, ?, ?)")
//				.itemPreparedStatementSetter(new CustomerItemPreparedStatementSetter())
//				.build();
//	}
//
	@Bean
	public JdbcBatchItemWriter<Customer> jdbcCustomerWriter(DataSource dataSource) throws Exception {
		return new JdbcBatchItemWriterBuilder<Customer>()
				.dataSource(dataSource)
				.sql("INSERT INTO CUSTOMER (first_name, " +
						"middle_initial, " +
						"last_name, " +
						"address, " +
						"city, " +
						"state, " +
						"zip) VALUES (:firstName, " +
						":middleInitial, " +
						":lastName, " +
						":address, " +
						":city, " +
						":state, " +
						":zip)")
				.beanMapped()
				.build();
	}

	@Bean
	public Step jdbcFormatStep() throws Exception {
		return this.stepBuilderFactory.get("jpaFormatStep")
				.<Customer, Customer>chunk(10)
				.reader(customerFileReader(null))
				.writer(jdbcCustomerWriter(null))
				.build();
	}

	@Bean
	public Job jdbcFormatJob() throws Exception {
		return this.jobBuilderFactory.get("jdbcFormatJob")
				.start(jdbcFormatStep())
				.build();
	}
}
