package com.mls.logistics.controller;

import com.mls.logistics.domain.Order;
import com.mls.logistics.dto.request.CreateOrderRequest;
import com.mls.logistics.dto.response.OrderResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * REST controller exposing Order-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the OrderService.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Constructor-based dependency injection.
     *
     * @param orderService service layer for order operations
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders.
     *
     * GET /api/orders
     *
     * @return list of orders
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService
                .getAllOrders()
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves an order by its identifier.
     *
     * GET /api/orders/{id}
     *
     * @param id order identifier
    * @return order if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService
                .getOrderById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    /**
     * Creates a new order.
     *
     * POST /api/orders
     *
     * @param request DTO containing order data
     * @return created order with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(createdOrder));
    }
}