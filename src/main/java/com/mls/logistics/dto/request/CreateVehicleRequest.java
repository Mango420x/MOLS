package com.mls.logistics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new Vehicle.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a vehicle, separating API contracts from
 * domain entities.
 */
public class CreateVehicleRequest {

    @NotBlank(message = "Vehicle type is required")
    @Size(min = 2, max = 50, message = "Vehicle type must be between 2 and 50 characters")
    private String type;

    @Positive(message = "Vehicle capacity must be greater than 0")
    private int capacity;

    @NotBlank(message = "Vehicle status is required")
    @Size(min = 2, max = 50, message = "Vehicle status must be between 2 and 50 characters")
    private String status;

    /**
     * Default constructor for deserialization.
     */
    public CreateVehicleRequest() {
    }

    /**
     * Constructs a CreateVehicleRequest with all fields.
     *
     * @param type vehicle type
     * @param capacity vehicle capacity
     * @param status vehicle status
     */
    public CreateVehicleRequest(String type, int capacity, String status) {
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}