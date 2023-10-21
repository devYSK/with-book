package com.ys.itemreader.delimiter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.ys.itemreader.domain.Customer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// @Configuration
public class DelimitedJobConfiguration {

	private JobBuilderFactory jobBuilderFactory;

	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DelimitedJobConfiguration(final JobBuilderFactory jobBuilderFactory, final StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	// @Bean
	// @StepScope
	// public FlatFileItemReader<Customer> customerItemReader(
	// 	@Value("#{jobParameters['customerFile']}") Resource inputFile) {
	// 	return new FlatFileItemReaderBuilder<Customer>()
	// 		.name("customerItemReader")
	// 		.delimited()
	// 		.names(new String[] {"firstName",
	// 			"middleInitial",
	// 			"lastName",
	// 			"addressNumber",
	// 			"street",
	// 			"city",
	// 			"state",
	// 			"zipCode"})
	// 		.fieldSetMapper(new CustomerFieldSetMapper())
	// 		.resource(inputFile)
	// 		.build();
	// }

	@Bean
	@StepScope
	public FlatFileItemReader<Customer> customerItemReader(
		@Value("#{jobParameters['customerFile']}") Resource inputFile) {
		return new FlatFileItemReaderBuilder<Customer>()
			.name("customerItemReader")
			.lineTokenizer(new CustomerFileLineTokenizer())
			.fieldSetMapper(new CustomerFieldSetMapper())
			.resource(inputFile)
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
			.reader(customerItemReader(null))
			.writer(itemWriter())
			.build();
	}

	@Bean
	public Job delimitedJob() {
		return this.jobBuilderFactory.get("delimitedJob")
			.start(copyFileStep())
			.build();
	}
}
