package com.ys.itemwriter.valid;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.ys.itemwriter.domain.Customer;
import com.ys.itemwriter.domain.UniqueLastNameValidator;

@Configuration
public class ValidationJobConfig {

	public final JobBuilderFactory jobBuilderFactory;

	public final StepBuilderFactory stepBuilderFactory;

	public ValidationJobConfig(final JobBuilderFactory jobBuilderFactory, final StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Customer> customerItemReader(
			@Value("#{jobParameters['customerFile']}") Resource inputFile) {

		return new FlatFileItemReaderBuilder<Customer>()
				.name("customerItemReader")
				.delimited()
				.names(new String[] {"firstName",
						"middleInitial",
						"lastName",
						"address",
						"city",
						"state",
						"zip"})
				.targetType(Customer.class)
				.resource(inputFile)
				.build();
	}

	@Bean
	public ItemWriter<Customer> itemWriter() {
		return (items) -> items.forEach(System.out::println);
	}

	@Bean
	public UniqueLastNameValidator validator() {
		UniqueLastNameValidator uniqueLastNameValidator = new UniqueLastNameValidator();

		uniqueLastNameValidator.setName("validator");

		return uniqueLastNameValidator;
	}

	@Bean
	public ValidatingItemProcessor<Customer> customerValidatingItemProcessor() {
		return new ValidatingItemProcessor<>(validator());
	}

//	@Bean
//	public BeanValidatingItemProcessor<Customer> customerValidatingItemProcessor() {
//		return new BeanValidatingItemProcessor<>();
//	}

	@Bean
	public Step copyFileStep() {

		return this.stepBuilderFactory.get("copyFileStep")
				.<Customer, Customer>chunk(5)
				.reader(customerItemReader(null))
				.processor(customerValidatingItemProcessor())
				.writer(itemWriter())
				.stream(validator())
				.build();
	}

	@Bean
	public Job job() throws Exception {
		return this.jobBuilderFactory.get("job")
				.start(copyFileStep())
				.build();
	}
}

