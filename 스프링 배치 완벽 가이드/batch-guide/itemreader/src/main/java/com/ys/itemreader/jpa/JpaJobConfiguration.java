package com.ys.itemreader.jpa;

import java.util.Collections;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ys.itemreader.domain.Customer;

@Configuration
public class JpaJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

//	@Bean
//	@StepScope
//	public JpaPagingItemReader<Customer> customerItemReader(
//			EntityManagerFactory entityManagerFactory,
//			@Value("#{jobParameters['city']}") String city) {
//
//		return new JpaPagingItemReaderBuilder<Customer>()
//				.name("customerItemReader")
//				.entityManagerFactory(entityManagerFactory)
//				.queryString("select c from Customer c where c.city = :city")
//				.parameterValues(Collections.singletonMap("city", city))
//				.build();
//	}

	@Bean
	@StepScope
	public JpaPagingItemReader<Customer> customerItemReader(
			EntityManagerFactory entityManagerFactory,
			@Value("#{jobParameters['city']}") String city) {

		// CustomerByCityQueryProvider queryProvider =
		// 		new CustomerByCityQueryProvider();
		// queryProvider.setCityName(city);

		return new JpaPagingItemReaderBuilder<Customer>()
				.name("customerItemReader")
				.entityManagerFactory(entityManagerFactory)
				// .queryProvider(queryProvider)
				.parameterValues(Collections.singletonMap("city", city))
				.build();
	}

	@Bean
	public ItemWriter<Customer> itemWriter() {
		return (items) -> items.forEach(System.out::println);
	}

	@Bean
	public Step copyFileStep() {
		return this.stepBuilderFactory.get("copyFileStep")
				.<Customer, Customer>chunk(10)
				.reader(customerItemReader(null, null))
				.writer(itemWriter())
				.build();
	}

	@Bean
	public Job jpaJob() {
		return this.jobBuilderFactory.get("jpaJob")
				.start(copyFileStep())
				.build();
	}



}

