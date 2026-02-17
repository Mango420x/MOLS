package com.mls.logistics.exception;

/**
 * Exception thrown when a requested quantity exceeds available stock.
 *
 * Thrown by StockService when an adjustment would result in negative stock,
 * and by OrderItemService when an order item requests more than what is available.
 * Caught by GlobalExceptionHandler and converted to 409 Conflict.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}