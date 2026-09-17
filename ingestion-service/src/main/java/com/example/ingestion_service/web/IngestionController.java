package com.example.ingestion_service.web;

import com.example.ingestion_service.model.TransactionRequest;
import com.example.ingestion_service.service.IdempotencyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class IngestionController {

    private final IdempotencyService idempotencyService;

    public IngestionController(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
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

        return ResponseEntity.accepted().body(transactionRequest);
    }
}
