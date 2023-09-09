package com.ys.batchguide;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
									 .start(step1())
									 .next(step2())
									 .build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
									  .tasklet((contribution, chunkContext) -> {
										  System.out.println("람다!");
										  return RepeatStatus.FINISHED;
									  })
									  .listener(promotionListener())
									  .build();
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
									  .tasklet(new HelloWorldTaskLet())
									  .listener(promotionListener())
									  .build();
	}

	@Bean
	public StepExecutionListener promotionListener() {
		final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();

		listener.setKeys(new String[] {"name"});

		return listener;
	}
}
