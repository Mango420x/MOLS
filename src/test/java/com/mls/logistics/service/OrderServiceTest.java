package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Unit;
import com.mls.logistics.dto.request.CreateOrderRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 *
 * Tests business logic without requiring database or Spring context.
 * Uses Mockito to mock repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        Unit unit = new Unit();
        unit.setId(1L);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUnit(unit);
        testOrder.setDateCreated(LocalDate.of(2024, 1, 1));
        testOrder.setStatus("CREATED");
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderService.getAllOrders();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("CREATED");
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrderById_WhenExists_ShouldReturnOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderService.getOrderById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("CREATED");
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.getOrderById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void createOrder_WithValidRequest_ShouldReturnCreatedOrder() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(1L, LocalDate.now(), "CREATED");
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order result = orderService.createOrder(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CREATED");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleteOrder_WhenExists_ShouldDeleteSuccessfully() {
        // Given
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOrder_WhenNotExists_ShouldThrowException() {
        // Given
        when(orderRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id: '999'");

        verify(orderRepository, times(1)).existsById(999L);
        verify(orderRepository, never()).deleteById(any());
    }
}
