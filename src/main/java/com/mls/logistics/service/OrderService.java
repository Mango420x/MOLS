package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Order-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves all registered orders.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its identifier.
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Creates a new order.
     * 
     * Business rules can be added here in the future
     * (e.g. order validation, price calculations).
     */
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
}
