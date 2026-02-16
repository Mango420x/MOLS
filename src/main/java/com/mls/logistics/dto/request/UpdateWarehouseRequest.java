package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Warehouse.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateWarehouseRequest {

    @Size(min = 2, max = 100, message = "Warehouse name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 200, message = "Warehouse location must be between 2 and 200 characters")
    private String location;

    public UpdateWarehouseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}