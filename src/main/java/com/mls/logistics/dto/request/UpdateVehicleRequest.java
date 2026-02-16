package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Vehicle.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateVehicleRequest {

    @Size(min = 2, max = 50, message = "Vehicle type must be between 2 and 50 characters")
    private String type;

    @Positive(message = "Vehicle capacity must be greater than 0")
    private Integer capacity;

    @Size(min = 2, max = 50, message = "Vehicle status must be between 2 and 50 characters")
    private String status;

    public UpdateVehicleRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}