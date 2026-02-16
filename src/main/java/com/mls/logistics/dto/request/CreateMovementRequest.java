package com.mls.logistics.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new Movement.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a movement, separating API contracts from
 * domain entities.
 */
public class CreateMovementRequest {

    @NotNull(message = "Stock ID is required")
    private Long stockId;

    @NotBlank(message = "Movement type is required")
    @Size(min = 2, max = 50, message = "Movement type must be between 2 and 50 characters")
    private String type;

    @Positive(message = "Movement quantity must be greater than 0")
    private int quantity;

    @NotNull(message = "Movement date and time are required")
    private LocalDateTime dateTime;

    /**
     * Default constructor for deserialization.
     */
    public CreateMovementRequest() {
    }

    /**
     * Constructs a CreateMovementRequest with all fields.
     *
     * @param stockId stock identifier
     * @param type movement type
     * @param quantity quantity affected
     * @param dateTime movement timestamp
     */
    public CreateMovementRequest(Long stockId, String type, int quantity, LocalDateTime dateTime) {
        this.stockId = stockId;
        this.type = type;
        this.quantity = quantity;
        this.dateTime = dateTime;
    }

    // Getters and setters

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}