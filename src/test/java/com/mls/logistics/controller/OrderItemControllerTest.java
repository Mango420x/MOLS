package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.security.service.AppUserService;
import com.mls.logistics.security.service.JwtService;
import com.mls.logistics.service.OrderItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Integration tests for OrderItemController.
 *
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserService appUserService;

    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        Order order = new Order();
        order.setId(1L);

        Resource resource = new Resource();
        resource.setId(1L);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(order);
        testOrderItem.setResource(resource);
        testOrderItem.setQuantity(10);
    }

    @Test
    @WithMockUser
    void getAllOrderItems_ShouldReturnOrderItemsList() throws Exception {
        // Given
        when(orderItemService.getAllOrderItems()).thenReturn(Arrays.asList(testOrderItem));

        // When & Then
        mockMvc.perform(get("/api/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(10));

        verify(orderItemService, times(1)).getAllOrderItems();
    }

    @Test
    @WithMockUser
    void getOrderItemById_WhenExists_ShouldReturnOrderItem() throws Exception {
        // Given
        when(orderItemService.getOrderItemById(1L)).thenReturn(Optional.of(testOrderItem));

        // When & Then
        mockMvc.perform(get("/api/order-items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));

        verify(orderItemService, times(1)).getOrderItemById(1L);
    }

    @Test
    @WithMockUser
    void getOrderItemById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(orderItemService.getOrderItemById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/order-items/999"))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).getOrderItemById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrderItem_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateOrderItemRequest request = new CreateOrderItemRequest(1L, 1L, 10);
        when(orderItemService.createOrderItem(any(CreateOrderItemRequest.class))).thenReturn(testOrderItem);

        // When & Then
        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(10));

        verify(orderItemService, times(1)).createOrderItem(any(CreateOrderItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrderItem_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given
        CreateOrderItemRequest request = new CreateOrderItemRequest(null, null, 0);

        // When & Then
        mockMvc.perform(post("/api/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderItemService, never()).createOrderItem(any(CreateOrderItemRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrderItem_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(orderItemService).deleteOrderItem(1L);

        // When & Then
        mockMvc.perform(delete("/api/order-items/1"))
                .andExpect(status().isNoContent());

        verify(orderItemService, times(1)).deleteOrderItem(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrderItem_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("OrderItem", "id", 999L))
                .when(orderItemService).deleteOrderItem(999L);

        // When & Then
        mockMvc.perform(delete("/api/order-items/999"))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).deleteOrderItem(999L);
    }
}
