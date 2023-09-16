package com.ys.practice;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoTest {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	void 몽고_컬렉션_테스트() {
		// given
		DBObject object = BasicDBObjectBuilder.start().add("Manning", "Spring Boot In Practice").get();
		// when

		mongoTemplate.save(object, "collection");
		// then
		Assertions.assertThat(mongoTemplate.findAll(DBObject.class, "collection"))
			.extracting("Manning")
			.containsOnly("Spring Boot In Practice");

	}
}
