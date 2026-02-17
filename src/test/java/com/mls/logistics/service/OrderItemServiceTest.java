package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.OrderItem;
import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateOrderItemRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderItemService.
 *
 * Tests business logic without requiring database or Spring context.
 * Uses Mockito to mock repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrderItemService orderItemService;

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
    void getAllOrderItems_ShouldReturnAllOrderItems() {
        // Given
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemRepository.findAll()).thenReturn(orderItems);

        // When
        List<OrderItem> result = orderItemService.getAllOrderItems();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuantity()).isEqualTo(10);
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void getOrderItemById_WhenExists_ShouldReturnOrderItem() {
        // Given
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));

        // When
        Optional<OrderItem> result = orderItemService.getOrderItemById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(10);
        verify(orderItemRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderItemById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<OrderItem> result = orderItemService.getOrderItemById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(orderItemRepository, times(1)).findById(999L);
    }

    @Test
    void createOrderItem_WithValidRequest_ShouldReturnCreatedOrderItem() {
        // Given
        CreateOrderItemRequest request = new CreateOrderItemRequest(1L, 1L, 10);
        when(stockService.getTotalAvailableQuantity(1L)).thenReturn(100);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        OrderItem result = orderItemService.createOrderItem(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(10);
        verify(stockService, times(1)).getTotalAvailableQuantity(1L);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void deleteOrderItem_WhenExists_ShouldDeleteSuccessfully() {
        // Given
        when(orderItemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderItemRepository).deleteById(1L);

        // When
        orderItemService.deleteOrderItem(1L);

        // Then
        verify(orderItemRepository, times(1)).existsById(1L);
        verify(orderItemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrderItem_WhenNotExists_ShouldThrowException() {
        // Given
        when(orderItemRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> orderItemService.deleteOrderItem(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("OrderItem not found with id: '999'");

        verify(orderItemRepository, times(1)).existsById(999L);
        verify(orderItemRepository, never()).deleteById(any());
    }
}
