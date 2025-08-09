package com.payment.platform.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a transaction entity stored in the PostgreSQL database.
 * Mapped to the "transactions" table.
 */
@Table("transactions")
public record TransactionEntity(
       @Id String transactionId,
       BigDecimal amount,
       String currency,
       String customerId,
       Instant timestamp
) {}
