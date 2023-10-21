package com.ys.itemreader.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.ys.itemreader.domain.Customer;

// @Configuration
public class JdbcPagingJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	public JdbcPagingJobConfiguration(final JobBuilderFactory jobBuilderFactory,
		final StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<Customer> customerItemReader(
		DataSource dataSource,
		PagingQueryProvider queryProvider,
		@Value("#{jobParameters['city']}") String city) {

		Map<String, Object> parameterValues = new HashMap<>(1);
		// parameterValues.put("city", city);

		return new JdbcPagingItemReaderBuilder<Customer>()
			.name("customerItemReader")
			.dataSource(dataSource)
			.queryProvider(queryProvider)
			// .parameterValues(parameterValues)
			.pageSize(10)
			.rowMapper(new CustomerRowMapper())
			.build();
	}

	@Bean
	public SqlPagingQueryProviderFactoryBean pagingQueryProvider(DataSource dataSource) {
		SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();

		factoryBean.setDataSource(dataSource);
		factoryBean.setSelectClause("select *");
		factoryBean.setFromClause("from CUSTOMER");
		// factoryBean.setWhereClause("where city = :city");

		factoryBean.setSortKey("lastName");

		return factoryBean;
	}

	@Bean
	public ItemWriter<Customer> itemWriter() {
		return (items) -> items.forEach(System.out::println);
	}

	@Bean
	public Step copyFileStep() {
		return this.stepBuilderFactory.get("copyFileStep")
			.<Customer, Customer>chunk(10)
			.reader(customerItemReader(null, null, null))
			.writer(itemWriter())
			.listener(new CustomChunkListener())
			.build();
	}

	@Bean
	public Job jdbcPagingJob() {
		return this.jobBuilderFactory.get("jdbcPagingJob")
			.start(copyFileStep())
			.build();
	}

	@Component
	public static class CustomChunkListener implements ChunkListener {

		@Override
		public void beforeChunk(ChunkContext context) {
			System.out.println("나 읽는다!");
		}

		@Override
		public void afterChunk(ChunkContext context) {
			StepExecution stepExecution = context.getStepContext().getStepExecution();
			int count = stepExecution.getWriteCount();
			System.out.println("청크 처리 완료: 청크 " + count + " 항목 처리됨");
		}

		@Override
		public void afterChunkError(ChunkContext context) {
			// 청크 처리 중 오류 발생시 수행할 로직
		}
	}

}

