package com.payment.platform.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

/**
 * Configuration for R2DBC (Reactive Relational Database Connectivity).
 * Sets up database initialization with schema.sql.
 */
@Configuration
@EnableR2dbcAuditing
public class R2dbcConfig {

    /**
     * Initializes the database schema using schema.sql.
     *
     * @param connectionFactory The R2DBC connection factory.
     * @return ConnectionFactoryInitializer for database setup.
     */
    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")
        ));
        return initializer;
    }
}
