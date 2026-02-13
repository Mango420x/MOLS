package com.mls.logistics.controller;

import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.dto.response.OrderItemResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * REST controller exposing OrderItem-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the OrderItemService.
 */
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * Constructor-based dependency injection.
     *
     * @param orderItemService service layer for order item operations
     */
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    /**
     * Retrieves all order items.
     *
     * GET /api/order-items
     *
     * @return list of order items
     */
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        List<OrderItemResponse> orderItems = orderItemService
                .getAllOrderItems()
                .stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderItems);
    }

    /**
     * Retrieves an order item by its identifier.
     *
     * GET /api/order-items/{id}
     *
     * @param id order item identifier
    * @return order item if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {
        OrderItem orderItem = orderItemService
                .getOrderItemById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "id", id));
        return ResponseEntity.ok(OrderItemResponse.from(orderItem));
    }

    /**
     * Creates a new order item.
     *
     * POST /api/order-items
     *
     * @param request DTO containing order item data
     * @return created order item with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<OrderItemResponse> createOrderItem(@RequestBody CreateOrderItemRequest request) {
        OrderItem createdOrderItem = orderItemService.createOrderItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderItemResponse.from(createdOrderItem));
    }
}