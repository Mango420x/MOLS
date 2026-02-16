package com.mls.logistics.dto.request;

import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for updating an existing OrderItem.
 * 
 * All fields are optional - only provided fields will be updated.
 */
public class UpdateOrderItemRequest {

    @Positive(message = "Order ID must be a positive number")
    private Long orderId;

    @Positive(message = "Resource ID must be a positive number")
    private Long resourceId;

    @Positive(message = "Order item quantity must be greater than 0")
    private Integer quantity;

    public UpdateOrderItemRequest() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}