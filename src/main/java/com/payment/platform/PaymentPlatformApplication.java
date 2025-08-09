package com.payment.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main entry point for the payment Platform application.
 * Enables Kafka listener and R2DBC repositories
 */

@SpringBootApplication
@EnableKafka
@EnableR2dbcRepositories
public class PaymentPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentPlatformApplication.class, args);
    }
}
