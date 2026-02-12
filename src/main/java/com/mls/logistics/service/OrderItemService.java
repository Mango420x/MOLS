package com.mls.logistics.service;

import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for OrderItem-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Retrieves all registered order items.
     */
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    /**
     * Retrieves an order item by its identifier.
     */
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    /**
     * Creates a new order item.
     * 
     * Business rules can be added here in the future
     * (e.g. item quantity validation).
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}
