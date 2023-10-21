package com.ys.rest_batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class QuartzJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	private AtomicInteger repeatCount = new AtomicInteger();

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
			.tasklet((stepContribution, chunkContext) -> {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss");

				System.out.println("step 1 ran!. repeatCount : " + repeatCount.incrementAndGet()
				+ ", " + LocalDateTime.now().format(formatter));
				return RepeatStatus.FINISHED;
			})
			.build();
	}

}
