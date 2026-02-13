package com.mls.logistics.service;

import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for OrderItem-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    /**
     * Creates a new order item from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing order item data
     * @return created order item entity
     */
    @Transactional
    public OrderItem createOrderItem(CreateOrderItemRequest request) {
        OrderItem orderItem = new OrderItem();

        Order order = new Order();
        order.setId(request.getOrderId());

        Resource resource = new Resource();
        resource.setId(request.getResourceId());

        orderItem.setOrder(order);
        orderItem.setResource(resource);
        orderItem.setQuantity(request.getQuantity());

        return orderItemRepository.save(orderItem);
    }
}
