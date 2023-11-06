package com.ys.application.config;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.StringUtils;

import com.ys.application.domain.Customer;
import com.ys.application.domain.CustomerAddressUpdate;
import com.ys.application.domain.CustomerContactUpdate;
import com.ys.application.domain.CustomerNameUpdate;
import com.ys.application.domain.CustomerUpdate;
import com.ys.application.domain.Statement;
import com.ys.application.domain.Transaction;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ImportJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job importJob() throws Exception {
		return this.jobBuilderFactory.get("importJob")
			.start(importCustomerUpdates())
			.next(importTransactions())
			.next(applyTransactions())
			.next(generateStatements(null))
			.build();
	}

	@Bean
	public Step importCustomerUpdates() throws Exception {
		return this.stepBuilderFactory.get("importCustomerUpdates")
			.<CustomerUpdate, CustomerUpdate>chunk(100)
			.reader(customerUpdateItemReader(null))
			.processor(customerValidatingItemProcessor(null))
			.writer(customerUpdateItemWriter())
			.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<CustomerUpdate> customerUpdateItemReader(
		@Value("#{jobParameters['customerUpdateFile']}") Resource inputFile) throws Exception {

		// System.out.println(inputFile.getFile().getName());

		final var classPathResource = new ClassPathResource("data/customer_update_shuffled.csv");

		return new FlatFileItemReaderBuilder<CustomerUpdate>()
			.name("customerUpdateItemReader")
			.resource(classPathResource)
			.lineTokenizer(customerUpdatesLineTokenizer())
			.fieldSetMapper(customerUpdateFieldSetMapper())
			.build();
	}

	@Bean
	public LineTokenizer customerUpdatesLineTokenizer() throws Exception {
		DelimitedLineTokenizer recordType1 = new DelimitedLineTokenizer();

		recordType1.setNames("recordId", "customerId", "firstName", "middleName", "lastName");

		recordType1.afterPropertiesSet();

		DelimitedLineTokenizer recordType2 = new DelimitedLineTokenizer();

		recordType2.setNames("recordId", "customerId", "address1", "address2", "city", "state", "postalCode");

		recordType2.afterPropertiesSet();

		DelimitedLineTokenizer recordType3 = new DelimitedLineTokenizer();

		recordType3.setNames("recordId", "customerId", "emailAddress", "homePhone", "cellPhone", "workPhone",
			"notificationPreference");

		recordType3.afterPropertiesSet();

		Map<String, LineTokenizer> tokenizers = new HashMap<>(3);
		tokenizers.put("1*", recordType1);
		tokenizers.put("2*", recordType2);
		tokenizers.put("3*", recordType3);

		PatternMatchingCompositeLineTokenizer lineTokenizer = new PatternMatchingCompositeLineTokenizer();

		lineTokenizer.setTokenizers(tokenizers);

		return lineTokenizer;
	}

	@Bean
	public FieldSetMapper<CustomerUpdate> customerUpdateFieldSetMapper() {
		return fieldSet -> {
			switch (fieldSet.readInt("recordId")) {
				case 1:
					return new CustomerNameUpdate(
						fieldSet.readLong("customerId"),
						fieldSet.readString("firstName"),
						fieldSet.readString("middleName"),
						fieldSet.readString("lastName"));
				case 2:
					return new CustomerAddressUpdate(
						fieldSet.readLong("customerId"),
						fieldSet.readString("address1"),
						fieldSet.readString("address2"),
						fieldSet.readString("city"),
						fieldSet.readString("state"),
						fieldSet.readString("postalCode"));
				case 3:
					String rawPreference = fieldSet.readString("notificationPreference");

					Integer notificationPreference = null;

					if (StringUtils.hasText(rawPreference)) {
						notificationPreference = Integer.parseInt(rawPreference);
					}

					return new CustomerContactUpdate(
						fieldSet.readLong("customerId"),
						fieldSet.readString("emailAddress"),
						fieldSet.readString("homePhone"),
						fieldSet.readString("cellPhone"),
						fieldSet.readString("workPhone"),
						notificationPreference);
				default:
					throw new IllegalArgumentException("Invalid record type was found:" + fieldSet.readInt("recordId"));
			}
		};
	}

	// ItemProcessor
	@Bean
	public ValidatingItemProcessor<CustomerUpdate> customerValidatingItemProcessor(CustomerItemValidator validator) {
		ValidatingItemProcessor<CustomerUpdate> customerValidatingItemProcessor = new ValidatingItemProcessor<>(
			validator);

		customerValidatingItemProcessor.setFilter(true);

		return customerValidatingItemProcessor;
	}

	// 고객 정보 갱신 Writer
	@Bean
	public ClassifierCompositeItemWriter<CustomerUpdate> customerUpdateItemWriter() {

		CustomerUpdateClassifier classifier =
			new CustomerUpdateClassifier(
				customerNameUpdateItemWriter(null),
				customerAddressUpdateItemWriter(null),
				customerContactUpdateItemWriter(null));

		ClassifierCompositeItemWriter<CustomerUpdate> compositeItemWriter =
			new ClassifierCompositeItemWriter<>();

		compositeItemWriter.setClassifier(classifier);

		return compositeItemWriter;
	}

	@Bean
	public JdbcBatchItemWriter<CustomerUpdate> customerNameUpdateItemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
			.beanMapped()
			.sql("update customer " +
				"set first_name = coalesce(:firstName, first_name), " +
				"middle_name = coalesce(:middleName, middle_name), " +
				"last_name = coalesce(:lastName, last_name) " +
				"where customer_id = :customerId")
			.dataSource(dataSource)
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<CustomerUpdate> customerAddressUpdateItemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
			.beanMapped()
			.sql("update customer set " +
				"address1 = coalesce(:address1, address1), " +
				"address2 = coalesce(:address2, address2), " +
				"city = coalesce(:city, city), " +
				"state = coalesce(:state, state), " +
				"postal_code = coalesce(:postalCode, postal_code) " +
				"where customer_id = :customerId")
			.dataSource(dataSource)
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<CustomerUpdate> customerContactUpdateItemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<CustomerUpdate>()
			.beanMapped()
			.sql("update customer set " +
				"email_address = coalesce(:emailAddress, email_address), " +
				"home_phone = coalesce(:homePhone, home_phone), " +
				"cell_phone = coalesce(:cellPhone, cell_phone), " +
				"work_phone = coalesce(:workPhone, work_phone), " +
				"notification_pref = coalesce(:notificationPreferences, notification_pref) " +
				"where customer_id = :customerId")
			.dataSource(dataSource)
			.build();
	}

	// ---------------------------- 2번째 next() ImportTransactionsJob
	@Bean
	public Step importTransactions() {
		return this.stepBuilderFactory.get("importTransactions")
			.<Transaction, Transaction>chunk(100)
			.reader(transactionItemReader())
			.writer(transactionItemWriter(null))
			.build();
	}

	@Bean
	@StepScope
	public StaxEventItemReader<Transaction> transactionItemReader() {
		Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
		unmarshaller.setClassesToBeBound(Transaction.class);

		final var classPathResource = new ClassPathResource("/data/transactions.xml");

		return new StaxEventItemReaderBuilder<Transaction>()
			.name("fooReader")
			.resource(classPathResource)
			.addFragmentRootElements("transaction")
			.unmarshaller(unmarshaller)
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<Transaction> transactionItemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Transaction>()
			.dataSource(dataSource)
			.sql("insert into transaction (transaction_id, " +
				"account_account_id, " +
				"description, " +
				"credit, " +
				"debit, " +
				"timestamp) values (:transactionId, " +
				":accountId, " +
				":description, " +
				":credit, " +
				":debit, " +
				":timestamp) " +
				"on duplicate key update " +
				"account_account_id = :accountId, " +
				"description = :description, " +
				"credit = :credit, " +
				"debit = :debit, " +
				"timestamp = :timestamp")
			.beanMapped()
			.build();
	}

	// # ---------------- 거래정보 기억하기 Job
	@Bean
	public Step applyTransactions() {
		return this.stepBuilderFactory.get("applyTransactions")
			.<Transaction, Transaction>chunk(100)
			.reader(applyTransactionReader(null))
			.writer(applyTransactionWriter(null))
			.faultTolerant()
			.skip(Exception.class)
			.skipLimit(2000)
			.build();
	}

	@Bean
	public JdbcCursorItemReader<Transaction> applyTransactionReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Transaction>()
			.name("applyTransactionReader")
			.dataSource(dataSource)
			.sql("select transaction_id, " +
				"account_account_id, " +
				"description, " +
				"credit, " +
				"debit, " +
				"timestamp " +
				"from transaction " +
				"order by timestamp")
			.rowMapper((resultSet, i) ->
				new Transaction(
					resultSet.getLong("transaction_id"),
					resultSet.getLong("account_account_id"),
					resultSet.getString("description"),
					resultSet.getBigDecimal("credit"),
					resultSet.getBigDecimal("debit"),
					resultSet.getTimestamp("timestamp")))
			.build();
	}

	@Bean
	public JdbcBatchItemWriter<Transaction> applyTransactionWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Transaction>()
			.dataSource(dataSource)
			.sql("UPDATE account SET " +
				"balance = balance + :transactionAmount " +
				"WHERE account_id = :accountId")
			.beanMapped()
			.build();
	}

	// # -------------- 마지막
	@Bean
	public Step generateStatements(AccountItemProcessor itemProcessor) {
		return this.stepBuilderFactory.get("generateStatements")
			.<Statement, Statement>chunk(1)
			.reader(statementItemReader(null))
			.processor(itemProcessor)
			.writer(statementItemWriter(null))
			.build();
	}

	@Bean
	public JdbcCursorItemReader<Statement> statementItemReader(DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Statement>()
			.name("statementItemReader")
			.dataSource(dataSource)
			.sql("SELECT * FROM customer")
			.rowMapper((resultSet, i) -> {
				Customer customer = new Customer(resultSet.getLong("customer_id"),
					resultSet.getString("first_name"),
					resultSet.getString("middle_name"),
					resultSet.getString("last_name"),
					resultSet.getString("address1"),
					resultSet.getString("address2"),
					resultSet.getString("city"),
					resultSet.getString("state"),
					resultSet.getString("postal_code"),
					resultSet.getString("ssn"),
					resultSet.getString("email_address"),
					resultSet.getString("home_phone"),
					resultSet.getString("cell_phone"),
					resultSet.getString("work_phone"),
					resultSet.getInt("notification_pref"));

				return new Statement(customer);
			})
			.build();
	}

	@Bean
	@StepScope
	public MultiResourceItemWriter<Statement> statementItemWriter(
		@Value("#{jobParameters['outputDirectory']}") String outputDir) {

		// 파일 접두사로 사용할 리소스 설정
		String filePrefix = outputDir + "output"; // 예: /Users/ysk/tmp/output
		Resource resource = new FileSystemResource(filePrefix);

		return new MultiResourceItemWriterBuilder<Statement>()
			.name("statementItemWriter")
			.resource(resource)
			.itemCountLimitPerResource(1)
			.delegate(individualStatementItemWriter())
			.build();
	}
	@Bean
	public FlatFileItemWriter<Statement> individualStatementItemWriter() {
		FlatFileItemWriter<Statement> itemWriter = new FlatFileItemWriter<>();

		itemWriter.setName("individualStatementItemWriter");
		itemWriter.setHeaderCallback(new StatementHeaderCallback());
		itemWriter.setLineAggregator(new StatementLineAggregator());

		return itemWriter;
	}
}
