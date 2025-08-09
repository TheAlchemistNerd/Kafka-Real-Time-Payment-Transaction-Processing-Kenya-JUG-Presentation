package com.payment.platform.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents an aggregated transaction summary.
 * Used for the query API endpoint.
 */
public record TransactionSummary (
    String currency,
    long transactionCount,
    BigDecimal totalVolume,
    Instant lastUpdated
) {}
