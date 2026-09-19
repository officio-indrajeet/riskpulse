package com.example.risk_scoring_service.model;

import java.math.BigDecimal;

/**
 * Deserialized shape of a transaction consumed from the {@code transactions.raw}
 * Kafka topic.
 * <p>
 * Field-for-field mirror of the Ingestion Service's own {@code TransactionRequest}
 * record (no shared module between the two services yet), minus its Bean Validation
 * annotations -- those only apply to validating inbound HTTP requests, not to
 * deserializing an already-accepted Kafka message.
 */
public record TransactionRequest(
        String transactionId,
        String accountId,
        BigDecimal amount,
        String currency,
        String merchantId,
        Long timestampEpochMillis
) {}
