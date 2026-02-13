package com.mls.logistics.dto.request;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new Order.
 * 
 * This class defines the structure of data accepted by the API
 * when creating an order, separating API contracts from
 * domain entities.
 */
public class CreateOrderRequest {

    private Long unitId;
    private LocalDate dateCreated;
    private String status;

    /**
     * Default constructor for deserialization.
     */
    public CreateOrderRequest() {
    }

    /**
     * Constructs a CreateOrderRequest with all fields.
     *
     * @param unitId unit identifier
     * @param dateCreated creation date
     * @param status order status
     */
    public CreateOrderRequest(Long unitId, LocalDate dateCreated, String status) {
        this.unitId = unitId;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    // Getters and setters

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
