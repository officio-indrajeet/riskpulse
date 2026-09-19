package com.example.ingestion_service.model;

import com.example.ingestion_service.validation.ValidCurrency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


/**
 * Immutable representation of an inbound transaction submitted for risk scoring.
 * <p>
 * Carries its own Bean Validation constraints so invalid requests are rejected
 * before they reach any business logic (idempotency check, Kafka publish). Mirrored
 * (without the validation annotations) by the Risk Scoring Service's own copy of
 * this record, since the two services don't yet share a common module.
 */
public record TransactionRequest(
        @NotBlank String transactionId,
        @NotBlank String accountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @ValidCurrency String currency,
        @NotBlank String merchantId,
        @NotNull Long timestampEpochMillis
) {}
