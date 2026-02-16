package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Unit.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateUnitRequest {

    @Size(min = 2, max = 100, message = "Unit name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 200, message = "Unit location must be between 2 and 200 characters")
    private String location;

    public UpdateUnitRequest() {
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