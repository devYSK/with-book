package com.ys.batchguide;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import net.bytebuddy.implementation.bytecode.Throw;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
public class HelloWorldJobConfig {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job helloWorldJob() {
		return this.jobBuilderFactory.get("helloWorldJob")
									 .start(helloWorldStep())
									 .validator(validator())
									 .incrementer(new RunIdIncrementer())
									 .build();
	}

	@Bean
	public Step helloWorldStep() {
		return this.stepBuilderFactory
			.get("helloWorldStep")
			.tasklet(helloWorldTaskLet(null))
			.build();
	}

	@StepScope
	@Bean
	public Tasklet helloWorldTaskLet(@Value("#{jobParameters['name']}") String name) {
		return (contribution, chunkContext) -> {
			System.out.println("Hello, World!");

			System.out.println(String.format("hello!232341243 %s", name));
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public CompositeJobParametersValidator validator() {
		final CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

		final DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(
			new String[] {"fileName"}, new String[] {"name"});

		defaultJobParametersValidator.afterPropertiesSet();

		validator.setValidators(
			List.of(new ParameterValidator(), defaultJobParametersValidator)
		);

		return validator;
	}

	public static class ParameterValidator implements JobParametersValidator {

		@Override
		public void validate(JobParameters parameters) throws JobParametersInvalidException {
			final String fileName = parameters.getString("fileName");

			if (!StringUtils.hasText(fileName)) {
				throw new JobParametersInvalidException("");
			}
		}

	}

	public static class DailyJobTimeStamper implements JobParametersIncrementer {

		@Override
		public JobParameters getNext(JobParameters parameters) {

			return new JobParametersBuilder(parameters)
				.addDate("currentDate", new Date())
				.toJobParameters();
		}
	}

}
