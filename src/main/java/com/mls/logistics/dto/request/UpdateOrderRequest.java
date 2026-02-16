package com.mls.logistics.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Order.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateOrderRequest {

    @Positive(message = "Unit ID must be a positive number")
    private Long unitId;

    private LocalDate dateCreated;

    @Size(min = 2, max = 50, message = "Order status must be between 2 and 50 characters")
    private String status;

    public UpdateOrderRequest() {
    }

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