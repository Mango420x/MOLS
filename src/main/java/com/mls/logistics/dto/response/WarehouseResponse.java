package com.mls.logistics.dto.response;

import com.mls.logistics.domain.Warehouse;

/**
 * Data Transfer Object for Warehouse responses.
 * 
 * This class defines the structure of warehouse data returned by the API,
 * allowing control over exactly what fields are exposed to clients.
 */
public class WarehouseResponse {

    private Long id;
    private String name;
    private String location;

    /**
     * Default constructor for serialization.
     */
    public WarehouseResponse() {
    }

    /**
     * Constructs a WarehouseResponse with all fields.
     *
     * @param id warehouse identifier
     * @param name warehouse name
     * @param location warehouse location
     */
    public WarehouseResponse(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    /**
     * Creates a WarehouseResponse from a Warehouse entity.
     * 
     * This static factory method converts domain entities to DTOs,
     * decoupling the API from the persistence layer.
     *
     * @param warehouse the warehouse entity
     * @return WarehouseResponse DTO
     */
    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getLocation()
        );
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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