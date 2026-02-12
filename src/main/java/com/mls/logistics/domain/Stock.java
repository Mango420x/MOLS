package com.mls.logistics.domain;

import jakarta.persistence.*;
import java.util.List;

/**
 * Represents the quantity of a Resource available in a specific Warehouse.
 */
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The resource stored */
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    /** The warehouse where the stock is located */
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    /** Quantity available (cannot be negative) */
    private int quantity;

    /** Historical movements of this stock */
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<Movement> movements;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

    public Warehouse getWarehouse() { return warehouse; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public List<Movement> getMovements() { return movements; }
    public void setMovements(List<Movement> movements) { this.movements = movements; }
}
