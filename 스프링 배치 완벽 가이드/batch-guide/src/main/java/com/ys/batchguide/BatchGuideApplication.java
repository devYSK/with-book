package com.ys.batchguide;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class BatchGuideApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchGuideApplication.class, args);
	}

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job helloWorldJob() {
		return this.jobBuilderFactory.get("helloWorldJob")
									 .start(helloWorldStep())
									 .build();
	}

	@Bean
	public Step helloWorldStep() {
		return this.stepBuilderFactory.get("helloWorldStep")
									  .tasklet((contribution, chunkContext) -> {
										  System.out.println("Hello, World!");
										  return RepeatStatus.FINISHED;
									  })
									  .build();
	}

}
