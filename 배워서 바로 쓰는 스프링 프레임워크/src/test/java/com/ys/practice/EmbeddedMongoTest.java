package com.ys.practice;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.MongodStarter;
import de.flapdoodle.embed.process.runtime.Network;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DataMongoTest
@TestPropertySource(properties = "property=A")
@ExtendWith(SpringExtension.class)
@DirtiesContext
class EmbeddedMongoTest {

	@Test
	void example(@Autowired final MongoTemplate mongoTemplate) {
		mongoTemplate.getDb().createCollection("deleteMe");
		long count = mongoTemplate.getDb().getCollection("deleteMe").countDocuments(Document.parse("{}"));

		assertThat(mongoTemplate.getDb()).isNotNull();
		assertThat(count).isEqualTo(0L);
	}


	@TestConfiguration
	public static class TransactionalConfig {

		@Bean
		MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
			return new MongoTransactionManager(dbFactory);
		}

		@Bean
		MongodArguments mongodArguments() {
			return MongodArguments.builder()
								  .replication(Storage.of("test", 10))
								  .build();
		}
	}
}
