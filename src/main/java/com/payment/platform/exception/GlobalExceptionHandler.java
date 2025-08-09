package com.payment.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

/**
 * Global exception handler for the application.
 * Provides centralized exception handling for various error scenarios.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles InvalidTransactionException, returning a BAD_REQUEST status.
     *
     * @param ex The InvalidTransactionException that was thrown.
     * @return A ResponseEntity with a BAD_REQUEST status and the exception message.
     */
    @ExceptionHandler(InvalidTransactionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<String>> handleInvalidTransactionException (InvalidTransactionException ex) {
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    /**
     * Handles all other unexpected exceptions, returning an INTERNAL_SERVER_ERROR status.
     *
     * @param ex The unexpected exception that was thrown.
     * @return A ResponseEntity with an INTERNAL_SERVER_ERROR status and a generic error message.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<String>> handleAllExceptions(Exception ex) {
        // In a real application, you might log the exception details for debugging
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage()));
    }
}
