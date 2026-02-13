package com.mls.logistics.dto.request;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a new Movement.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a movement, separating API contracts from
 * domain entities.
 */
public class CreateMovementRequest {

    private Long stockId;
    private String type;
    private int quantity;
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