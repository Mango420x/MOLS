package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Unit;
import com.mls.logistics.dto.request.CreateOrderRequest;
import com.mls.logistics.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Order-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Creates a new order from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing order data
     * @return created order entity
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();

        Unit unit = new Unit();
        unit.setId(request.getUnitId());

        order.setUnit(unit);
        order.setDateCreated(request.getDateCreated());
        order.setStatus(request.getStatus());

        return orderRepository.save(order);
    }
}
