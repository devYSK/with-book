package com.ys.itemreader.fixed;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.ys.itemreader.domain.Customer;

// @Configuration
public class FixedWidthJobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	public FixedWidthJobConfiguration(
		final JobBuilderFactory jobBuilderFactory,
		final StepBuilderFactory stepBuilderFactory) {

		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Customer> fixedWidthItemReader(
		@Value("#{jobParameters['customerFile']}") Resource inputFile) {

		return new FlatFileItemReaderBuilder<Customer>()
			.name("fixedWidthItemReader")
			.resource(inputFile)
			.fixedLength()
			.columns(new Range[] { // 각 Range 인스턴스는 파싱해야 할 칼럼의 시작 위치와 종료 위치를 나타낸다
				new Range(1, 11),
				new Range(12, 12),
				new Range(13, 22),
				new Range(23, 26),
				new Range(27, 46),
				new Range(47, 62),
				new Range(63, 64),
				new Range(65, 69)})
			.names("firstName", "middleInitial", "lastName",
				"addressNumber", "street", "city", "state", "zipCode")
			.targetType(Customer.class)
			.build();
	}

	@Bean
	public ItemWriter<Customer> fixedWidthItemWriter() {
		return items -> items.forEach(System.out::println);
	}

	@Bean
	public Step fixedWidthStep(ChunkListener customChunkListener) {
		return this.stepBuilderFactory.get("fixedWidthStep")
			.<Customer, Customer>chunk(10)
			.reader(fixedWidthItemReader(null))
			.writer(fixedWidthItemWriter())
			.listener(customChunkListener)
			.build();
	}

	@Bean
	public Job fixedWidthJob() {
		return this.jobBuilderFactory.get("fixedWidthJob")
			.start(fixedWidthStep(null))
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