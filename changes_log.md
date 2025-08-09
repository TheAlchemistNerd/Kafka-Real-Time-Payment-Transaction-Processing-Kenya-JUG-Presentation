## Summary of Changes

Here is a summary of the changes made to the project:

1.  **Updated `Dockerfile` to use a Maven image for building:**
    *   Changed `FROM eclipse-temurin:21-jdk-jammy AS build` to `FROM maven:3.9-eclipse-temurin-21 AS build` to fix the "Maven not found" error during the Docker build.

2.  **Adjusted `docker-compose.yml` to resolve port conflicts:**
    *   Changed the host port for the `zookeeper` service from `2181` to `28181` to avoid port conflicts on the host machine.

3.  **Corrected a typo in `KafkaConfig.java`:**
    *   Changed `@Value("${spring.kafka.boostrap-servers}")` to `@Value("${spring.kafka.bootstrap-servers}")` to fix a property resolution error.

4.  **Corrected a typo in `application.yml`:**
    *   Changed `auto-offset-reset: eartliest` to `auto-offset-reset: earliest` to fix a Kafka consumer configuration error.

5.  **Corrected a syntax error in `schema.sql`:**
    *   Changed `transaction_id VARCHAR(255) PRIMARY,` to `transaction_id VARCHAR(255) PRIMARY KEY,` to fix a SQL syntax error during table creation.
