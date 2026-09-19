package com.example.ingestion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the RiskPulse Ingestion Service.
 * <p>
 * Boots the Spring application context that exposes the REST endpoint for accepting
 * incoming transactions, validates them, checks for duplicates via Redis-backed
 * idempotency, and publishes accepted transactions onto the {@code transactions.raw}
 * Kafka topic for downstream processing (e.g. by the Risk Scoring Service).
 */
@SpringBootApplication
public class IngestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestionServiceApplication.class, args);
	}

}
