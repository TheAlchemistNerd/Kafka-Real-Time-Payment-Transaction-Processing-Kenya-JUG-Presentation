package com.payment.platform.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents an incoming transaction event from Kafka.
 * This is an immutable record for clarity and conciseness.
 */
public record TransactionEvent(
        String transactionId,
        BigDecimal amount,
        String currency,
        String customerId,
        Instant timestamp
) {}