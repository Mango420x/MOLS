package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Shipment;
import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.ShipmentRepository;
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
 * Unit tests for ShipmentService.
 *
 * Tests business logic without requiring database or Spring context.
 * Uses Mockito to mock repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment testShipment;

    @BeforeEach
    void setUp() {
        Order order = new Order();
        order.setId(1L);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        testShipment = new Shipment();
        testShipment.setId(1L);
        testShipment.setOrder(order);
        testShipment.setVehicle(vehicle);
        testShipment.setWarehouse(warehouse);
        testShipment.setStatus("PLANNED");
    }

    @Test
    void getAllShipments_ShouldReturnAllShipments() {
        // Given
        List<Shipment> shipments = Arrays.asList(testShipment);
        when(shipmentRepository.findAll()).thenReturn(shipments);

        // When
        List<Shipment> result = shipmentService.getAllShipments();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PLANNED");
        verify(shipmentRepository, times(1)).findAll();
    }

    @Test
    void getShipmentById_WhenExists_ShouldReturnShipment() {
        // Given
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(testShipment));

        // When
        Optional<Shipment> result = shipmentService.getShipmentById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("PLANNED");
        verify(shipmentRepository, times(1)).findById(1L);
    }

    @Test
    void getShipmentById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Shipment> result = shipmentService.getShipmentById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(shipmentRepository, times(1)).findById(999L);
    }

    @Test
    void createShipment_WithValidRequest_ShouldReturnCreatedShipment() {
        // Given
        CreateShipmentRequest request = new CreateShipmentRequest(1L, 1L, 1L, "PLANNED");
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(testShipment);

        // When
        Shipment result = shipmentService.createShipment(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PLANNED");
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    void deleteShipment_WhenExists_ShouldDeleteSuccessfully() {
        // Given
        when(shipmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(shipmentRepository).deleteById(1L);

        // When
        shipmentService.deleteShipment(1L);

        // Then
        verify(shipmentRepository, times(1)).existsById(1L);
        verify(shipmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteShipment_WhenNotExists_ShouldThrowException() {
        // Given
        when(shipmentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> shipmentService.deleteShipment(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Shipment not found with id: '999'");

        verify(shipmentRepository, times(1)).existsById(999L);
        verify(shipmentRepository, never()).deleteById(any());
    }
}
