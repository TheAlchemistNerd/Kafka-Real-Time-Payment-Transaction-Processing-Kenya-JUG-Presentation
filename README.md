# A Kafka-Centric Real-Time Payment Transaction Processing Platform

## Project Overview

This project is a high-throughput, event-driven backbone for processing financial transactions in real-time. It's a core backend engine built to handle a large volume of transactions with low latency, high reliability, and complete observability. The core of this system is an event-driven architecture (EDA) built on Apache Kafka, which is further refined using the Command Query Responsibility Segregation (CQRS) pattern to address conflicting performance requirements for writes and reads.

## Architecture

The system is designed to handle two distinct and often conflicting needs: high-throughput writes (ingesting a massive stream of transactions) and fast analytical reads (performing complex queries on aggregated data).

### Initial Approach: A Simple Event-Driven Architecture (EDA)

A standard EDA is a good starting point, using Kafka to decouple services and provide resilience.

### The Solution: Command Query Responsibility Segregation (CQRS)

To solve the contention challenge, the architecture is evolved by formally separating the write and read responsibilities using the CQRS pattern.

## Code Explanation

Here's a breakdown of the project's code, organized by functionality:

**1. Application Entry Point (`PaymentPlatformApplication.java`)**

*   This is the main class that bootstraps the Spring Boot application.
*   `@SpringBootApplication`: A convenience annotation that adds all of the following:
    *   `@Configuration`: Tags the class as a source of bean definitions for the application context.
    *   `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
    *   `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com/payment/platform` package, allowing it to find and register them.
*   `@EnableKafka`: Enables Kafka listener annotated endpoints.
*   `@EnableR2dbcRepositories`: Enables Spring Data R2DBC repositories.

**2. Kafka Configuration (`KafkaConfig.java`)**

*   This class is responsible for configuring the Kafka consumer.
*   `@Configuration`: Indicates that this class contains Spring configuration.
*   `@EnableKafka`: Enables detection of `@KafkaListener` annotations on any Spring-managed bean.
*   `transactionConsumerFactory()`: This bean creates a `ConsumerFactory` which is responsible for creating Kafka consumers. It sets the bootstrap servers, group ID, and deserializers for the key and value. The `JsonDeserializer` is configured to deserialize the incoming message into a `TransactionEvent` object.
*   `kafkaListenerContainerFactory()`: This bean creates a `ConcurrentKafkaListenerContainerFactory` which is used by the `@KafkaListener` annotation to create the message listener container.

**3. R2DBC Configuration (`R2dbcConfig.java`)**

*   This class configures the reactive database connection.
*   `@Configuration`: Indicates that this class contains Spring configuration.
*   `@EnableR2dbcAuditing`: Enables auditing of entities, which can be used to automatically populate fields like `createdDate` and `lastModifiedDate`.
*   `initializer()`: This bean initializes the database with the `schema.sql` file. This is useful for development and testing, but you might want to use a more robust database migration tool like Flyway or Liquibase for production.

**4. Controller (`TransactionController.java`)**

*   This is a standard Spring WebFlux REST controller.
*   `@RestController`: A convenience annotation that combines `@Controller` and `@ResponseBody`.
*   `@RequestMapping("/api/transactions")`: Maps all requests starting with `/api/transactions` to this controller.
*   `getTransactionSummary()`: This method handles GET requests to `/api/transactions/summary`. It calls the `TransactionService` to get the transaction summary and returns it as a `Mono<TransactionSummary>`.

**5. Service (`TransactionService.java`)**

*   This is the core of the application's business logic.
*   `@Service`: Indicates that this class is a Spring service.
*   `processAndPersistTransaction()`: This method is the heart of the write-side of your application. It receives a `TransactionEvent`, validates it, converts it to a `TransactionEntity`, and saves it to the database using the `TransactionRepository`. It also increments the appropriate Micrometer counters for success or failure.
*   `getTransactionSummary()`: This method is the heart of the read-side of the application. It fetches all transactions from the database, aggregates them in memory, and returns a `TransactionSummary`. **This is the part that will change significantly when you implement CQRS.**
*   `isValidTransaction()`: A private helper method to validate the incoming transaction event.
*   `toEntity()`: A private helper method to convert a `TransactionEvent` to a `TransactionEntity`.

**6. Repository (`TransactionRepository.java`)**

*   This is a Spring Data R2DBC repository.
*   `@Repository`: Indicates that this class is a Spring repository.
*   `extends R2dbcRepository<TransactionEntity, String>`: This enables the repository to perform CRUD operations on `TransactionEntity` objects. Spring Data automatically implements the methods of this interface.
*   `findByCustomerId()`: A custom query method that finds all transactions for a given customer ID.

**7. Models (`TransactionEntity.java`, `TransactionEvent.java`, `TransactionSummary.java`)**

*   These are all Java `record` classes, which is a great choice for immutable data transfer objects (DTOs).
*   `TransactionEvent`: Represents the data coming from Kafka.
*   `TransactionEntity`: Represents the data stored in the database.
*   `TransactionSummary`: Represents the data returned by the query API.

**8. Exception Handling (`GlobalExceptionHandler.java`, `InvalidTransactionException.java`)**

*   `InvalidTransactionException`: A custom exception for invalid transaction data.
*   `GlobalExceptionHandler`: A `@ControllerAdvice` class that handles exceptions globally. This is a good practice for centralizing exception handling logic.

**9. Telemetry (`MetricConfig.java`)**

*   This class defines and registers custom Micrometer counters for monitoring the application. This is excellent for observability.

## Recommendations for CQRS

The project is on the right track for implementing CQRS. Here are some recommendations will guide the next phase:

1.  **Separate Read and Write Models:** The project already started this by having `TransactionEntity` (write) and `TransactionSummary` (read). As we move to a full CQRS implementation, we will likely have more read models, each tailored to a specific query.

2.  **Separate Read and Write Databases (Optional but Recommended):** For maximum performance and scalability, consider using separate databases for the read and write models. The write database would be optimized for writes (e.g., normalized), and the read database would be optimized for reads (e.g., denormalized).

3.  **Create a Projection Service:** We will need a new service that listens to the same Kafka topic as your current `TransactionService`. This new service, let's call it `TransactionProjectionService`, will be responsible for updating the read models in the read database.

4.  **Update the `TransactionService`:** The `TransactionService` will no longer be responsible for querying data. Its `getTransactionSummary()` method will be removed. It will only be responsible for processing commands and saving them to the write database.

5.  **Create a `TransactionQueryService`:** We will need a new service that is responsible for querying the read database. The `TransactionController` will call this new service to get the data for the API.

6.  **Eventual Consistency:** This architecture will be eventually consistent. This is a trade-off made for the performance and scalability benefits of CQRS. It's important to understand and accept this trade-off.

## How to Run the Application

1.  **Prerequisites:**
    *   Java 21
    *   Maven
    *   Docker
    *   Docker Compose

2.  **Build the application:**
    ```bash
    mvn clean install
    ```

3.  **Run the application using Docker Compose:**
    ```bash
    docker-compose up --build
    ```
