package com.ys.batchguide;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class DemoApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private JobExplorer jobExplorer;

	@Bean
	public Tasklet explorerTasklet() {
		return new ExploringTasklet(this.jobExplorer);
	}

	@Bean
	public Step explorerStep() {
		return this.stepBuilderFactory.get("explorerStep")
									  .tasklet(explorerTasklet())
									  .build();
	}

	@Bean
	public Job explorerJob() {
		return this.jobBuilderFactory.get("explorerJob")
									 .start(explorerStep())
									 .build();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	public static class ExploringTasklet implements Tasklet {

		private JobExplorer explorer;

		public ExploringTasklet(JobExplorer explorer) {
			this.explorer = explorer;
		}

		public RepeatStatus execute(StepContribution stepContribution,
			ChunkContext chunkContext) {

			String jobName = chunkContext.getStepContext().getJobName();

			List<JobInstance> instances =
				explorer.getJobInstances(jobName,
					0,
					Integer.MAX_VALUE);

			System.out.println(
				String.format("There are %d job instances for the job %s",
					instances.size(),
					jobName));

			System.out.println("They have had the following results");
			System.out.println("************************************");

			for (JobInstance instance : instances) {
				List<JobExecution> jobExecutions =
					this.explorer.getJobExecutions(instance);

				System.out.println(
					String.format("Instance %d had %d executions",
						instance.getInstanceId(),
						jobExecutions.size()));

				for (JobExecution jobExecution : jobExecutions) {
					System.out.println(
						String.format("\tExecution %d resulted in Exit Status %s",
							jobExecution.getId(),
							jobExecution.getExitStatus()));
				}
			}

			return RepeatStatus.FINISHED;
		}
	}

}

