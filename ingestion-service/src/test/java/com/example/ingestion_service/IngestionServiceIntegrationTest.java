package com.example.ingestion_service;

import com.example.ingestion_service.model.TransactionRequest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
public class IngestionServiceIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.1");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    TestRestTemplate restTemplate;

    private Consumer<String, String> testConsumer;

    @BeforeEach
    void setUpConsumer() {
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-group", true);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        testConsumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        testConsumer.subscribe(Collections.singletonList("transactions.raw"));
    }

    @AfterEach
    void tearDownConsumer() {
        testConsumer.close();
    }

    @Test
    void validTransaction_returns202() {
        // POST a good TransactionRequest, assert 202
        TransactionRequest request = new TransactionRequest(
                "tx-integration-1", "acc-999", new BigDecimal("100.00"), "USD", "merch-1", System.currentTimeMillis());

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/v1/transactions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(testConsumer, "transactions.raw", Duration.ofSeconds(10));

        assertThat(record.key()).isEqualTo("acc-999");
        assertThat(record.value()).contains("tx-integration-1");
    }

    @Test
    void invalidCurrency_returns400() {
        // POST with a bad currency, assert 400
        TransactionRequest request = new TransactionRequest(
                "tx-integration-2", "acc-999", new BigDecimal("100.00"), "DUD", "merch-1", System.currentTimeMillis());

        ResponseEntity<String> response =
                restTemplate.postForEntity("/api/v1/transactions", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void duplicateTransactionId_returns409() {
        // POST the same transactionId twice, assert second is 409
        TransactionRequest request = new TransactionRequest(
                "tx-integration-3", "acc-999", new BigDecimal("100.00"), "USD", "merch-1", System.currentTimeMillis());

        ResponseEntity<String> first =
                restTemplate.postForEntity("/api/v1/transactions", request, String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        ResponseEntity<String> second =
                restTemplate.postForEntity("/api/v1/transactions", request, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

}
