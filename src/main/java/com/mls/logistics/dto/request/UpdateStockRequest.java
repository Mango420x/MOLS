package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object for updating an existing Stock.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateStockRequest {

    @Positive(message = "Resource ID must be a positive number")
    private Long resourceId;

    @Positive(message = "Warehouse ID must be a positive number")
    private Long warehouseId;

    @PositiveOrZero(message = "Stock quantity must be zero or greater")
    private Integer quantity;

    public UpdateStockRequest() {
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}