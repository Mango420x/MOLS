package com.mls.logistics.domain;

import jakarta.persistence.*;

/**
 * Represents a single resource requested in an Order.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Order this item belongs to */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /** Resource requested */
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    /** Quantity requested */
    private int quantity;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
