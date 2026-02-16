package com.mls.logistics.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for creating a new OrderItem.
 * 
 * This class defines the structure of data accepted by the API
 * when creating an order item, separating API contracts from
 * domain entities.
 */
public class CreateOrderItemRequest {

    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be a positive number")
    private Long orderId;

    @NotNull(message = "Resource ID is required")
    @Positive(message = "Resource ID must be a positive number")
    private Long resourceId;

    @Positive(message = "Order item quantity must be greater than 0")
    private int quantity;

    /**
     * Default constructor for deserialization.
     */
    public CreateOrderItemRequest() {
    }

    /**
     * Constructs a CreateOrderItemRequest with all fields.
     *
     * @param orderId order identifier
     * @param resourceId resource identifier
     * @param quantity requested quantity
     */
    public CreateOrderItemRequest(Long orderId, Long resourceId, int quantity) {
        this.orderId = orderId;
        this.resourceId = resourceId;
        this.quantity = quantity;
    }

    // Getters and setters

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
