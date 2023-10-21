package com.ys.transaction_batch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableBatchProcessing
@SpringBootApplication
public class TransactionBatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		Set<String> realArgs = new HashSet<>(Arrays.asList(args));

		realArgs.add("transactionFile=input/transactionFile.csv");
		realArgs.add("summaryFile=file:/Users/ysk/tmp/summaryFile3.csv");

		SpringApplication.run(TransactionBatchApplication.class, realArgs.toArray(new String[0]));

	}

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job transactionJob;


	@Override
	public void run(String... args) throws Exception {
		JobParametersBuilder parametersBuilder = new JobParametersBuilder();

		for (String arg : args) {
			System.out.println(arg);

			if (arg.startsWith("transactionFile=")) {
				parametersBuilder.addString("transactionFile", arg.split("=")[1]);
			} else if (arg.startsWith("summaryFile=")) {
				parametersBuilder.addString("summaryFile", arg.split("=")[1]);
			}
		}

		jobLauncher.run(transactionJob, parametersBuilder.toJobParameters());
	}

}
