package com.mls.logistics.dto.request;

/**
 * Data Transfer Object for creating a new Resource.
 * 
 * This class defines the structure of data accepted by the API
 * when creating a resource, separating API contracts from
 * domain entities.
 */
public class CreateResourceRequest {

    private String name;
    private String type;
    private String criticality;

    /**
     * Default constructor for deserialization.
     */
    public CreateResourceRequest() {
    }

    /**
     * Constructs a CreateResourceRequest with all fields.
     *
     * @param name resource name
     * @param type resource type
     * @param criticality criticality level
     */
    public CreateResourceRequest(String name, String type, String criticality) {
        this.name = name;
        this.type = type;
        this.criticality = criticality;
    }

    // Getters and setters

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
