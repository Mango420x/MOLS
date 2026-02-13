package com.mls.logistics.dto.response;

import com.mls.logistics.domain.Unit;

/**
 * Data Transfer Object for Unit responses.
 * 
 * This class defines the structure of unit data returned by the API,
 * allowing control over exactly what fields are exposed to clients.
 */
public class UnitResponse {

    private Long id;
    private String name;
    private String location;

    /**
     * Default constructor for serialization.
     */
    public UnitResponse() {
    }

    /**
     * Constructs a UnitResponse with all fields.
     *
     * @param id unit identifier
     * @param name unit name
     * @param location unit location
     */
    public UnitResponse(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    /**
     * Creates a UnitResponse from a Unit entity.
     * 
     * This static factory method converts domain entities to DTOs,
     * decoupling the API from the persistence layer.
     *
     * @param unit the unit entity
     * @return UnitResponse DTO
     */
    public static UnitResponse from(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getName(),
                unit.getLocation()
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
