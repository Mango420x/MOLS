package com.mls.logistics.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Form model for draft items on the "New order" screen.
 */
public class OrderDraftItemForm {

    @NotNull(message = "Resource is required")
    @Positive(message = "Resource must be a valid selection")
    private Long resourceId;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

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
