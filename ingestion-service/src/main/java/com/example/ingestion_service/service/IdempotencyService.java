package com.example.ingestion_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private static final String KEY_PREFIX = "txn:seen:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Atomically checks-and-marks a transactionId as seen, using Redis's SET NX EX
     * (setIfAbsent with a TTL) -- this is a single atomic operation, so two requests
     * with the same transactionId arriving at (almost) the same instant can never
     * both win the "first seen" race.
     *
     * @return true if this is the FIRST time we've seen this transactionId (proceed),
     *         false if it's a duplicate we've already accepted within the last 24h.
     */
    public boolean markIfFirstSeen(String transactionId) {
        Boolean wasAbsent = redisTemplate.opsForValue()
                .setIfAbsent(KEY_PREFIX + transactionId, "1", TTL);
        return Boolean.TRUE.equals(wasAbsent);
    }
}
