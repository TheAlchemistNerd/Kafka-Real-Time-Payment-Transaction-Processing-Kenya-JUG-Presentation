package com.payment.platform.service;

import com.payment.platform.exception.InvalidTransactionException;
import com.payment.platform.model.TransactionEntity;
import com.payment.platform.model.TransactionEvent;
import com.payment.platform.model.TransactionSummary;
import com.payment.platform.repository.TransactionRepository;
import com.payment.platform.telemetry.MetricConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Service layer for handling transaction processing and querying.
 * Contains business logic and interacts with the repository.
 */
@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final Counter transactionCreatedCounter;
    private final Counter transactionFailedCounter;


    /**
     * Constructs a new TransactionService.
     *
     * @param transactionRepository The repository for transaction entities.
     * @param kafkaTemplate The KafkaTemplate for sending messages.
     * @param meterRegistry The Micrometer MeterRegistry for metrics.
     */
    public TransactionService(TransactionRepository transactionRepository,
                              KafkaTemplate<String, TransactionEvent> kafkaTemplate,
                              MeterRegistry meterRegistry) {
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.transactionCreatedCounter = MetricConfig.transactionCreatedCounter(meterRegistry);
        this.transactionFailedCounter = MetricConfig.transactionFailedCounter(meterRegistry);
    }

    /**
     * Publishes a new transaction event to Kafka.
     *
     * @param event The TransactionEvent to publish.
     * @return A Mono of the published TransactionEvent.
     */
    public Mono<TransactionEvent> processTransactionEvent(TransactionEvent event) {
        log.info("Received transaction event: {}", event);
        return Mono.fromRunnable(() -> kafkaTemplate.send("transactions-topic", event.transactionId(), event))
                .thenReturn(event);
    }

    /**
     * Processes and persists a transaction event.
     * Validates the event before saving.
     *
     * @param event The TransactionEvent to process.
     * @return A Mono of the persisted TransactionEntity.
     */
    @KafkaListener(topics = "transactions-topic", groupId = "tx-persist-group")
    public Mono<TransactionEntity> processAndPersistTransaction(TransactionEvent event) {
        return Mono.just(event)
                .filter(this::isValidTransaction)
                .switchIfEmpty(Mono.error(new InvalidTransactionException("Invalid transaction event: " + event.transactionId())))
                .map(this::toEntity)
                .flatMap(transactionRepository::save)
                .doOnSuccess(s -> {
                    transactionCreatedCounter.increment();
                    log.info("Transaction persisted: {}", s.transactionId());
                })
                .doOnError(e -> {
                    transactionFailedCounter.increment();
                    log.error("Failed to process transaction: {}", e.getMessage());
                });
    }

    /**
     * Validates a transaction event.
     *
     * @param event The TransactionEvent to validate.
     * @return True if the transaction is valid, false otherwise.
     */
    private boolean isValidTransaction(TransactionEvent event) {
        return event.transactionId() != null && !event.transactionId().isBlank() &&
                event.amount() != null && event.amount().compareTo(BigDecimal.ZERO) > 0 &&
                event.currency() != null && !event.currency().isBlank() &&
                event.customerId() != null && !event.customerId().isBlank() &&
                event.timestamp() != null;
    }

    /**
     * Retrieves an aggregated summary of transactions.
     * In a simple EDA, this might involve querying all transactions and aggregating in memory.
     * In a real system, this would be optimized with proper database queries or a read model.
     *
     * @return A Mono of TransactionSummary.
     */
    public Mono<TransactionSummary> getTransactionSummary() {
        return transactionRepository.findAll()
                .collectList()
                .map(transactions -> {
                    long count = transactions.size();
                    BigDecimal totalVolume = transactions.stream()
                            .map(TransactionEntity::amount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // For simplicity, currency is hardcoded, in a real scenario, it would be dynamic
                    return new TransactionSummary("USD", count, totalVolume, Instant.now());
                });
    }
    /**
     * Converts a TransactionEvent to a TransactionEntity.
     *
     * @param event The TransactionEvent to convert.
     * @return The corresponding TransactionEntity.
     */
    private TransactionEntity toEntity(TransactionEvent event) {
        return new TransactionEntity(
                event.transactionId(),
                event.amount(),
                event.currency(),
                event.customerId(),
                event.timestamp()
        );
    }
}
