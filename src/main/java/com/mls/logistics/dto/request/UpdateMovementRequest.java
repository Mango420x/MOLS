package com.mls.logistics.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Movement.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateMovementRequest {

    @Positive(message = "Stock ID must be a positive number")
    private Long stockId;

    @Size(min = 2, max = 50, message = "Movement type must be between 2 and 50 characters")
    private String type;

    @Positive(message = "Movement quantity must be greater than 0")
    private Integer quantity;

    private LocalDateTime dateTime;

    public UpdateMovementRequest() {
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}