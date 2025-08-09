package com.payment.platform.repository;

import com.payment.platform.model.TransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Reactive repository for TransactionEntity.
 * Provides non-blocking database operations for transactions.
 */

@Repository
public interface TransactionRepository extends R2dbcRepository<TransactionEntity, String> {

    /**
     * Finds all transaction entities associated with a specific customer ID.
     *
     * @param customerId The ID of the customer.
     * @return A Flux of TransactionEntity for the given customer ID.
     */
    Flux<TransactionEntity> findByCustomerId(String customerId);
}
