package com.mls.logistics.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for creating a new Stock.
 * 
 * This class defines the structure of data accepted by the API
 * when creating stock, separating API contracts from
 * domain entities.
 */
public class CreateStockRequest {

    @NotNull(message = "Resource ID is required")
    @Positive(message = "Resource ID must be a positive number")
    private Long resourceId;

    @NotNull(message = "Warehouse ID is required")
    @Positive(message = "Warehouse ID must be a positive number")
    private Long warehouseId;

    @PositiveOrZero(message = "Stock quantity must be zero or greater")
    private int quantity;

    /**
     * Default constructor for deserialization.
     */
    public CreateStockRequest() {
    }

    /**
     * Constructs a CreateStockRequest with all fields.
     *
     * @param resourceId resource identifier
     * @param warehouseId warehouse identifier
     * @param quantity available quantity
     */
    public CreateStockRequest(Long resourceId, Long warehouseId, int quantity) {
        this.resourceId = resourceId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }

    // Getters and setters

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
