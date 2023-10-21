package com.ys.transaction_batch.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.ys.transaction_batch.domain.AccountSummary;
import com.ys.transaction_batch.domain.Transaction;
import com.ys.transaction_batch.domain.TransactionDao;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableBatchProcessing
@Configuration
// @SpringBootApplication
public class TransactionProcessingJob1 {

	// public static void main(String[] args) {
	// 	List<String> realArgs = new ArrayList<>(Arrays.asList(args));
	//
	// 	realArgs.add("transactionFile=input/transactionFile.csv");
	// 	realArgs.add("summaryFile=file:///Users/mminella/tmp/summaryFile3.csv");
	//
	// 	SpringApplication.run(TransactionProcessingJob.class, realArgs.toArray(new String[realArgs.size()]));
	// }

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public TransactionDao transactionDao(DataSource dataSource) {
		return new TransactionDaoSupport(dataSource);
	}

	@Bean
	public TransactionApplierProcessor transactionApplierProcessor() {
		return new TransactionApplierProcessor(transactionDao(null));
	}

	@Bean
	@StepScope
	public JdbcCursorItemReader<AccountSummary> accountSummaryReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<AccountSummary>()
			.name("accountSummaryReader")
			.dataSource(dataSource)
			.sql("SELECT ACCOUNT_NUMBER, CURRENT_BALANCE " +
				"FROM ACCOUNT_SUMMARY A " +
				"WHERE A.ID IN (" +
				"	SELECT DISTINCT T.ACCOUNT_SUMMARY_ID " +
				"	FROM TRANSACTION T) " +
				"ORDER BY A.ACCOUNT_NUMBER")
			.rowMapper((resultSet, rowNumber) -> {
				AccountSummary summary = new AccountSummary();

				summary.setAccountNumber(resultSet.getString("account_number"));
				summary.setCurrentBalance(resultSet.getDouble("current_balance"));

				return summary;
			})
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<AccountSummary> accountSummaryWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<AccountSummary>()
			.dataSource(dataSource)
			.itemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("UPDATE ACCOUNT_SUMMARY " +
				"SET CURRENT_BALANCE = :currentBalance " +
				"WHERE ACCOUNT_NUMBER = :accountNumber")
			.build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<AccountSummary> accountSummaryFileWriter(
		@Value("#{jobParameters['summaryFile']}") Resource summaryFile) {

		DelimitedLineAggregator<AccountSummary> lineAggregator =
			new DelimitedLineAggregator<>();
		BeanWrapperFieldExtractor<AccountSummary> fieldExtractor =
			new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"accountNumber", "currentBalance"});
		fieldExtractor.afterPropertiesSet();
		lineAggregator.setFieldExtractor(fieldExtractor);

		return new FlatFileItemWriterBuilder<AccountSummary>()
			.name("accountSummaryFileWriter")
			.resource(summaryFile)
			.lineAggregator(lineAggregator)
			.build();
	}

	@Bean
	public Step generateAccountSummaryStep() {
		return this.stepBuilderFactory.get("generateAccountSummaryStep")
			.<AccountSummary, AccountSummary>chunk(100)
			.reader(accountSummaryReader(null))
			.writer(accountSummaryFileWriter(null))
			.build();
	}

	@Bean
	public Step applyTransactionsStep() {
		return this.stepBuilderFactory.get("applyTransactionsStep")
			.<AccountSummary, AccountSummary>chunk(100)
			.reader(accountSummaryReader(null))
			.processor(transactionApplierProcessor())
			.writer(accountSummaryWriter(null))
			.build();
	}

	@Bean
	public Step importTransactionFileStep() {
		return this.stepBuilderFactory.get("importTransactionFileStep")
			.<Transaction, Transaction>chunk(100)
			.reader(transactionReader())
			.writer(transactionWriter(null))
			.allowStartIfComplete(true)
			.listener(transactionReader())
			.build();
	}

	@Bean
	@StepScope
	public TransactionReader transactionReader() {
		return new TransactionReader(fileItemReader(null));
	}

	@Bean
	@StepScope
	public FlatFileItemReader<FieldSet> fileItemReader(
		@Value("#{jobParameters['transactionFile']}") Resource inputFile) {
		return new FlatFileItemReaderBuilder<FieldSet>()
			.name("fileItemReader")
			.resource(inputFile)
			.lineTokenizer(new DelimitedLineTokenizer())
			.fieldSetMapper(new PassThroughFieldSetMapper())
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Transaction>()
			.itemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO TRANSACTION " +
				"(ACCOUNT_SUMMARY_ID, TIMESTAMP, AMOUNT) " +
				"VALUES ((SELECT ID FROM ACCOUNT_SUMMARY " +
				"	WHERE ACCOUNT_NUMBER = :accountNumber), " +
				":timestamp, :amount)")
			.dataSource(dataSource)
			.build();
	}


	@Bean
	public Job transactionJob() {
		return this.jobBuilderFactory.get("transactionJob") // Spring Batch의 JobBuilder 팩토리에서 "transactionJob" 이름으로 Job을 생성 시작합니다.
			.start(importTransactionFileStep()) // Job의 시작 단계로 "importTransactionFileStep" Step을 지정합니다.
			.on("STOPPED") // "importTransactionFileStep" Step의 실행 결과가 "STOPPED" 상태일 경우의 플로우를 정의합니다.
			.stopAndRestart(importTransactionFileStep()) // "STOPPED" 상태일 경우 "importTransactionFileStep" Step을 중지하고 다시 시작합니다.
			.from(importTransactionFileStep()) // "importTransactionFileStep" Step 이후의 플로우를 정의하기 시작합니다.
			.on("*") // "importTransactionFileStep" Step의 모든 실행 결과(어떤 상태든)에 대한 플로우를 정의합니다.
			.to(applyTransactionsStep()) // 모든 상태를 갖는 "importTransactionFileStep" 이후에 "applyTransactionsStep" Step을 실행합니다.
			.from(applyTransactionsStep()) // "applyTransactionsStep" Step 이후의 플로우를 정의하기 시작합니다.
			.next(generateAccountSummaryStep()) // "applyTransactionsStep" Step 이후에 "generateAccountSummaryStep" Step을 실행합니다.
			.end() // Job의 정의를 종료합니다.
			.build(); // Job을 구축하고 반환합니다.
	}


}
