package com.mls.logistics.dto.request;

/**
 * Data Transfer Object for creating a new Warehouse.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a warehouse, separating API contracts from
 * domain entities.
 */
public class CreateWarehouseRequest {

    private String name;
    private String location;

    /**
     * Default constructor for deserialization.
     */
    public CreateWarehouseRequest() {
    }

    /**
     * Constructs a CreateWarehouseRequest with all fields.
     *
     * @param name warehouse name
     * @param location warehouse location
     */
    public CreateWarehouseRequest(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters and setters

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