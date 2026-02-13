package com.mls.logistics.exception;

/**
 * Exception thrown when a request contains invalid data or violates business rules.
 * 
 * This exception is caught by the GlobalExceptionHandler and converted
 * to a 400 Bad Request HTTP response.
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Constructs a new InvalidRequestException with the specified detail message.
     *
     * @param message detailed message explaining what validation failed
     */
    public InvalidRequestException(String message) {
        super(message);
    }
}