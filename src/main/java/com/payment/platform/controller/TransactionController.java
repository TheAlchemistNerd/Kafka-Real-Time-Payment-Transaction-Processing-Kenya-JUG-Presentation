package com.payment.platform.controller;

import com.payment.platform.model.TransactionSummary;
import com.payment.platform.service.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.payment.platform.model.TransactionEvent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for transaction-related API endpoints.
 * Exposes endpoints for querying transaction summaries and processing new transactions.
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

    /**
     * Processes a new transaction event.
     *
     * @param event The TransactionEvent object containing transaction details.
     * @return A Mono of the processed TransactionEvent.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<TransactionEvent> processTransaction(@RequestBody TransactionEvent event) {
        return transactionService.processTransactionEvent(event);
    }
}
