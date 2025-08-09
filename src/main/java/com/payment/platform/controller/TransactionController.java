package com.payment.platform.controller;

import com.payment.platform.model.TransactionSummary;
import com.payment.platform.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for transaction-related API endpoints.
 * Exposes endpoints for querying transaction summaries.
 */

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Retrieves an aggregated summary of all transactions.
     *
     * @return A Mono of TransactionSummary.
     */
    @GetMapping("/summary")
    public Mono<TransactionSummary> getTransactionSummary() {
        return transactionService.getTransactionSummary();
    }
}
