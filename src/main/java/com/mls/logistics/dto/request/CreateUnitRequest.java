package com.mls.logistics.dto.request;

/**
 * Data Transfer Object for creating a new Unit.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a unit, separating API contracts from
 * domain entities.
 */
public class CreateUnitRequest {

    private String name;
    private String location;

    /**
     * Default constructor for deserialization.
     */
    public CreateUnitRequest() {
    }

    /**
     * Constructs a CreateUnitRequest with all fields.
     *
     * @param name unit name
     * @param location unit location
     */
    public CreateUnitRequest(String name, String location) {
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
