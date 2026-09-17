package com.example.ingestion_service.model;

import com.example.ingestion_service.validation.ValidCurrency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record TransactionRequest(
        @NotBlank String transactionId,
        @NotBlank String accountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @ValidCurrency String currency,
        @NotBlank String merchantId,
        @NotNull Long timestampEpochMillis
) {}
