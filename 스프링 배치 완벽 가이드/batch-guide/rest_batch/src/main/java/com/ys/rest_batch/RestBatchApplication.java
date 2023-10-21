package com.ys.rest_batch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ys.rest_batch.custom.TransactionProcessingJob;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EnableBatchProcessing
@SpringBootApplication
@RequiredArgsConstructor
public class RestBatchApplication {

	public static void main(String[] args) {
		// SpringApplication.run(RestBatchApplication.class, args);
		//

		List<String> realArgs = new ArrayList<>(Arrays.asList(args));

		realArgs.add("transactionFile=input/transactionFile.csv");
		realArgs.add("/Users/ysk/study/with-book/resources/summaryFile3.csv");

		SpringApplication.run(TransactionProcessingJob.class, realArgs.toArray(new String[realArgs.size()]));
	}

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	// @Bean
	// public Job job() {
	// 	return this.jobBuilderFactory.get("job")
	// 		.incrementer(new RunIdIncrementer())
	// 		.start(step1())
	// 		.build();
	// }
	//
	// @Bean
	// public Step step1() {
	// 	return this.stepBuilderFactory.get("step1")
	// 		.tasklet((stepContribution, chunkContext) -> {
	// 			System.out.println("step 1 ran today!");
	// 			return RepeatStatus.FINISHED;
	// 		})
	// 		.build();
	// }

	// @RestController
	// public static class JobLaunchingController {
	//
	// 	@Autowired
	// 	private JobLauncher jobLauncher;
	//
	// 	@Autowired
	// 	private ApplicationContext context;
	//
	// 	@Autowired
	// 	private JobExplorer jobExplorer;
	//
	// 	@PostMapping(path = "/run")
	// 	public ExitStatus runJob(@RequestBody JobLaunchRequest request) throws Exception {
	// 		Job job = this.context.getBean(request.getName(), Job.class);
	//
	// 		JobParameters jobParameters =
	// 			new JobParametersBuilder(request.getJobParameters(),
	// 				this.jobExplorer)
	// 				.getNextJobParameters(job)
	// 				.toJobParameters();
	//
	// 		return this.jobLauncher.run(job, jobParameters)
	// 			.getExitStatus();
	// 		//			Job job = this.context.getBean(request.getName(), Job.class);
	// 		//
	// 		//			return this.jobLauncher.run(job, request.getJobParameters()).getExitStatus();
	// 	}
	// }

	@Getter
	@RequiredArgsConstructor
	public static class JobLaunchRequest {
		private final String name;

		private final Properties jobParameters;

		public JobParameters getJobParameters() {
			Properties properties = new Properties();
			properties.putAll(this.jobParameters);

			return new JobParametersBuilder(properties)
				.toJobParameters();
		}
	}

}
