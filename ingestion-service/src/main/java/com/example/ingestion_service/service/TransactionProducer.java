package com.example.ingestion_service.service;

import com.example.ingestion_service.model.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;
    private final String topic;

    public TransactionProducer(
            KafkaTemplate<String, TransactionRequest> kafkaTemplate,
            @Value("${spring.kafka.topic.transactions-raw}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(TransactionRequest transactionRequest) {
        kafkaTemplate.send(topic, transactionRequest.accountId(), transactionRequest);
    }
}
