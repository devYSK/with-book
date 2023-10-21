package com.ys.itemreader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableBatchProcessing
@SpringBootApplication
public class ItemreaderApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(ItemreaderApplication.class, args);

		// ApplicationContext에서 JobLauncher와 Job을 가져옵니다.
		JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		Job readCustomerFileJob = ctx.getBean("jdbcPagingJob", Job.class);

		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addString("city", "true")
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			// Job 실행
			jobLauncher.run(readCustomerFileJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
