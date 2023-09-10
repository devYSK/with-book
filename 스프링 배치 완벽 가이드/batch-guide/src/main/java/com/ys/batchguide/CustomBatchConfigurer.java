package com.ys.batchguide;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CustomBatchConfigurer extends DefaultBatchConfigurer {

	@Qualifier("repositoryDataSource")
	private final DataSource dataSource;

	@Override
	protected JobRepository createJobRepository() throws Exception {
		JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();

		factoryBean.setDatabaseType(DatabaseType.MYSQL.getProductName());
		factoryBean.setTablePrefix("FOO_");
		factoryBean.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
		factoryBean.setDataSource(this.dataSource);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	@Qualifier("batchTransactionManager")
	private final PlatformTransactionManager transactionManager;

	@Override
	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	@Override
	protected JobExplorer createJobExplorer () throws Exception {
		JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
		factoryBean.setDataSource(this.dataSource);
		factoryBean.setTablePrefix("FOO_");
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}
}
