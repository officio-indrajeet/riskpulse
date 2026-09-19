package com.example.risk_scoring_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the RiskPulse Risk Scoring Service.
 * <p>
 * Boots the Spring application context that runs the Kafka Streams topology
 * consuming from {@code transactions.raw} (see {@code TopologyConfig}) and, as
 * scoring logic is added, computes and publishes risk scores for each transaction.
 */
@SpringBootApplication
public class RiskScoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskScoringServiceApplication.class, args);
    }
}
