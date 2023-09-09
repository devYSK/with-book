package com.ys.batchguide;

import java.util.Map;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorldTaskLet implements Tasklet {

	private static final String HELLO_WORLD = "Hello, %s";

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		final String name = (String)chunkContext.getStepContext()
												.getJobParameters()
												.get("name");

		ExecutionContext jobContext = chunkContext.getStepContext()
												  .getStepExecution()
												  .getJobExecution()
												  .getExecutionContext();

		final StepContext stepContext = chunkContext.getStepContext();
		final Map<String, Object> jobExecutionContext = stepContext.getJobExecutionContext();

		jobContext.put("user.name", name);

		System.out.println("name! " +  name);
		return RepeatStatus.FINISHED;
	}

}
