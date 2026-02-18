package com.mls.logistics.web;

/**
 * View model for draft items shown on the "New order" screen.
 */
public class OrderDraftItemView {

    private final int index;
    private final Long resourceId;
    private final String resourceName;
    private final String resourceType;
    private final int quantity;

    public OrderDraftItemView(int index, Long resourceId, String resourceName, String resourceType, int quantity) {
        this.index = index;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.quantity = quantity;
    }

    public int getIndex() {
        return index;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public int getQuantity() {
        return quantity;
    }
}
