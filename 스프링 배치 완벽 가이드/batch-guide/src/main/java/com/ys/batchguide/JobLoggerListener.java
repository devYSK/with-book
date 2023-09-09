package com.ys.batchguide;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class JobLoggerListener {

	private static String START_MESSAGE = "%s is execution 전";
	private static String END_MESSAGE = "%s 작업 완료. status : %s";

	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		System.out.println(String.format(START_MESSAGE, jobExecution.getJobInstance()
																	.getJobName()));
	}

	@AfterJob
	public void afterJob(JobExecution jobExecution) {

		System.out.println(String.format(END_MESSAGE,
			jobExecution.getJobInstance()
						.getJobName(),
			jobExecution.getStatus()
		));
	}

}
