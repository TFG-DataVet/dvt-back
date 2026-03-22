package com.datavet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DatavetApplicationTests {

	@Autowired
	private MongoTemplate mongoTemplate;

	@BeforeEach
	void cleanDatabase() {
		mongoTemplate.getDb().drop();
	}

	@Test
	void contextLoads() {
	}
}