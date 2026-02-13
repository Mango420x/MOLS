package com.mls.logistics.dto.request;

/**
 * Data Transfer Object for creating a new Vehicle.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a vehicle, separating API contracts from
 * domain entities.
 */
public class CreateVehicleRequest {

    private String type;
    private int capacity;
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