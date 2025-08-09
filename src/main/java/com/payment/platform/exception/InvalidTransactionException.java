package com.payment.platform.exception;

/**
 * Custom exception for invalid transaction data.
 * Thrown when a transaction event fails validation.
 */
public class InvalidTransactionException extends RuntimeException {

    /**
     * Constructs a new InvalidTransactionException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
}