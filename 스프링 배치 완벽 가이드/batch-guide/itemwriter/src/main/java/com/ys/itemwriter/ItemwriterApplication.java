package com.ys.itemwriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableBatchProcessing
public class ItemwriterApplication {

	public static void main(String[] args) {
		final var ctx = SpringApplication.run(ItemwriterApplication.class, args);

		JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
		Job readCustomerFileJob = ctx.getBean("jpaFormatJob", Job.class);

		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addString("customerFile", "classpath:data/customer.csv")  // 클래스패스에서의 입력 파일 경로
				// .addString("outputFile", "file:/Users/ysk/tmp/customer_output.csv")  // 클래스패스에 있는 출력 파일 경로
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

			// Job 실행
			jobLauncher.run(readCustomerFileJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
