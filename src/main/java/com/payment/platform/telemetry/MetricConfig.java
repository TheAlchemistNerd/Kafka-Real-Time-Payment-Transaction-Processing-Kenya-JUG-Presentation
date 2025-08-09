package com.payment.platform.telemetry;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application metrics using Micrometer.
 * Defines and registers custom counters for transaction processing.
 */
@Configuration
public class MetricConfig {
    /**
     * Creates and registers a counter for successfully processed transactions.
     *
     * @param meterRegistry The Micrometer MeterRegistry.
     * @return A Counter for successful transactions.
     */
    public static Counter transactionCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transaction.processed.total")
                .description("Total number of successfully processed transactions")
                .tag("status", "success")
                .register(meterRegistry);
    }

    /**
     * Creates and registers a counter for failed transaction processing attempts.
     *
     * @param meterRegistry The Micrometer MeterRegistry.
     * @return A Counter for failed transactions.
     */
    public static Counter transactionFailedCounter(MeterRegistry meterRegistry) {
        return Counter.builder("transactions.processed.failed")
                .description("Total number of failed transaction processing attempts")
                .tag("status", "failed")
                .register(meterRegistry);
    }
}
