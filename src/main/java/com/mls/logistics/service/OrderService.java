package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Unit;
import com.mls.logistics.dto.request.CreateOrderRequest;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.dto.request.UpdateOrderRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.OrderRepository;
import org.springframework.data.domain.Sort;
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
    private final OrderItemService orderItemService;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
    }

    /**
     * Retrieves all registered orders.
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getAllOrders(Sort sort) {
        return orderRepository.findAll(sort);
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

    /**
     * Creates an order and its items in a single database transaction.
     *
     * If any item fails validation (for example insufficient stock), nothing is saved.
     */
    @Transactional
    public Order createOrderWithItems(CreateOrderRequest request, List<CreateOrderItemRequest> items) {
        Order order = createOrder(request);

        if (items != null) {
            for (CreateOrderItemRequest item : items) {
                CreateOrderItemRequest toCreate = new CreateOrderItemRequest(
                        order.getId(),
                        item.getResourceId(),
                        item.getQuantity()
                );
                orderItemService.createOrderItem(toCreate);
            }
        }

        return order;
    }

    /**
     * Updates an existing order.
     * 
     * Only non-null fields from the request are updated.
     *
     * @param id order identifier
     * @param request update data
     * @return updated order
     * @throws ResourceNotFoundException if order doesn't exist
     */
    @Transactional
    public Order updateOrder(Long id, UpdateOrderRequest request) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (request.getUnitId() != null) {
            Unit unit = new Unit();
            unit.setId(request.getUnitId());
            order.setUnit(unit);
        }
        if (request.getDateCreated() != null) {
            order.setDateCreated(request.getDateCreated());
        }
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }

        return orderRepository.save(order);
    }

    /**
     * Deletes an order by ID.
     *
     * @param id order identifier
     * @throws ResourceNotFoundException if order doesn't exist
     */
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        orderRepository.deleteById(id);
    }
}
