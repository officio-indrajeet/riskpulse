package com.example.ingestion_service.web;

import com.example.ingestion_service.model.TransactionRequest;
import com.example.ingestion_service.service.IdempotencyService;
import com.example.ingestion_service.service.TransactionProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that accepts incoming transactions for fraud/risk scoring.
 * <p>
 * Exposes {@code POST /api/v1/transactions}: validates the request body, rejects
 * duplicates (HTTP 409) using {@link IdempotencyService}, and otherwise publishes
 * the transaction onto Kafka via {@link TransactionProducer} before returning
 * HTTP 202 Accepted.
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class IngestionController {

    private final IdempotencyService idempotencyService;
    private final TransactionProducer transactionProducer;

    public IngestionController(IdempotencyService idempotencyService, TransactionProducer transactionProducer) {
        this.idempotencyService = idempotencyService;
        this.transactionProducer = transactionProducer;
    }

    @PostMapping
    public ResponseEntity<?> ingestTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        boolean isFirstTimeSeen = idempotencyService.markIfFirstSeen(transactionRequest.transactionId());

        if (!isFirstTimeSeen) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "error", "Duplicate transactionId",
                            "transactionId", transactionRequest.transactionId()
                    ));
        }
        transactionProducer.send(transactionRequest);
        return ResponseEntity.accepted().body(transactionRequest);
    }
}
