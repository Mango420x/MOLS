package com.mls.logistics.web;

import java.io.Serializable;

/**
 * Draft item stored in HttpSession while building a new Order.
 */
public class OrderDraftItem implements Serializable {

    private Long resourceId;
    private int quantity;

    public OrderDraftItem() {
    }

    public OrderDraftItem(Long resourceId, int quantity) {
        this.resourceId = resourceId;
        this.quantity = quantity;
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
