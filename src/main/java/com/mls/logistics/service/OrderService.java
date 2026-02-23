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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
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

    private static final String STATUS_COMPLETED = "COMPLETED";

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

    public List<Order> getOrdersExcludingStatus(String excludedStatus, Sort sort) {
        if (excludedStatus == null || excludedStatus.isBlank()) {
            return orderRepository.findAll(sort);
        }
        return orderRepository.findByStatusNot(excludedStatus, sort);
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

    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    public long countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    /**
     * Fulfillment rate as a percentage of completed orders vs total orders.
     */
    public double getFulfillmentRate() {
        long total = orderRepository.count();
        if (total == 0) {
            return 0.0;
        }

        long completed = orderRepository.countByStatus(STATUS_COMPLETED);
        return (completed * 100.0) / total;
    }

    /**
     * Marks an order as completed.
     *
     * Used by shipment fulfillment: when a shipment is delivered, the parent order should be considered fulfilled.
     */
    @Transactional
    public void markOrderCompleted(Long orderId) {
        if (orderId == null) {
            return;
        }

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (STATUS_COMPLETED.equalsIgnoreCase(order.getStatus() != null ? order.getStatus().trim() : null)) {
            return;
        }

        order.setStatus(STATUS_COMPLETED);
        orderRepository.save(order);
    }

    /**
     * Returns pending orders older than the provided threshold (in days).
     *
     * The system currently uses CREATED and VALIDATED as pre-fulfillment statuses.
     */
    public List<Order> getStaleOrders(int daysThreshold) {
        LocalDate cutoff = LocalDate.now().minus(daysThreshold, ChronoUnit.DAYS);

        List<Order> results = new ArrayList<>();
        results.addAll(orderRepository.findByStatusAndDateCreatedBefore("CREATED", cutoff));
        results.addAll(orderRepository.findByStatusAndDateCreatedBefore("VALIDATED", cutoff));

        results.sort(Comparator
                .comparing(Order::getDateCreated, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Order::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        return results;
    }
}
