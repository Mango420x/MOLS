package com.mls.logistics.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a movement of stock (IN, OUT, TRANSFER) for audit purposes.
 */
@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stock affected by this movement */
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    /** Type of movement: ENTRY, EXIT, TRANSFER */
    private String type;

    /** Quantity affected */
    private int quantity;

    /** Timestamp of the movement */
    private LocalDateTime dateTime;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}
