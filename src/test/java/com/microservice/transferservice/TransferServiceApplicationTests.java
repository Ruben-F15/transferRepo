package com.microservice.transferservice;

import com.microservice.transferservice.kafka.config.KafkaConsumerConfig;
import com.microservice.transferservice.kafka.producer.TransferEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@ActiveProfiles("test")
class TransferServiceApplicationTests {

	@MockitoBean
	private TransferEventProducer transferEventProducer;

	@Container // ARRANCAMOS CONTENEDOR MONGO
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		mongoDBContainer.start();
		registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void contextLoads() {
	}

}
