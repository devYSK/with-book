package com.ys.practice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories
@EnableAsync
public class MongoConfig {

	@Bean
	public MongoClient mongoClient() {
		return new MongoClient("localhost");
	}


	@Bean
	public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
		return new MongoTemplate(factory);
	}

}
