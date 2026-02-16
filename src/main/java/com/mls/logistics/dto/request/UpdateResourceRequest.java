package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating an existing Resource.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateResourceRequest {

    @Size(min = 2, max = 100, message = "Resource name must be between 2 and 100 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Resource type must be between 2 and 50 characters")
    private String type;

    @Size(min = 2, max = 50, message = "Resource criticality must be between 2 and 50 characters")
    private String criticality;

    public UpdateResourceRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }
}