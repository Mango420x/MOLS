package com.mls.logistics.service;

import com.mls.logistics.domain.Movement;
import com.mls.logistics.domain.Stock;
import com.mls.logistics.dto.request.CreateMovementRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovementService.
 *
 * Tests business logic without requiring database or Spring context.
 * Uses Mockito to mock repository dependencies.
 */
@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private MovementService movementService;

    private Movement testMovement;

    @BeforeEach
    void setUp() {
        Stock stock = new Stock();
        stock.setId(1L);

        testMovement = new Movement();
        testMovement.setId(1L);
        testMovement.setStock(stock);
        testMovement.setType("IN");
        testMovement.setQuantity(5);
        testMovement.setDateTime(LocalDateTime.of(2024, 1, 1, 10, 0));
    }

    @Test
    void getAllMovements_ShouldReturnAllMovements() {
        // Given
        List<Movement> movements = Arrays.asList(testMovement);
        when(movementRepository.findAll()).thenReturn(movements);

        // When
        List<Movement> result = movementService.getAllMovements();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("IN");
        verify(movementRepository, times(1)).findAll();
    }

    @Test
    void getMovementById_WhenExists_ShouldReturnMovement() {
        // Given
        when(movementRepository.findById(1L)).thenReturn(Optional.of(testMovement));

        // When
        Optional<Movement> result = movementService.getMovementById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getType()).isEqualTo("IN");
        verify(movementRepository, times(1)).findById(1L);
    }

    @Test
    void getMovementById_WhenNotExists_ShouldReturnEmpty() {
        // Given
        when(movementRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Movement> result = movementService.getMovementById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(movementRepository, times(1)).findById(999L);
    }

    @Test
    void createMovement_WithValidRequest_ShouldReturnCreatedMovement() {
        // Given
        CreateMovementRequest request = new CreateMovementRequest(1L, "IN", 5, LocalDateTime.now());
        when(movementRepository.save(any(Movement.class))).thenReturn(testMovement);

        // When
        Movement result = movementService.createMovement(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("IN");
        verify(movementRepository, times(1)).save(any(Movement.class));
    }

    @Test
    void deleteMovement_WhenExists_ShouldDeleteSuccessfully() {
        // Given
        when(movementRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movementRepository).deleteById(1L);

        // When
        movementService.deleteMovement(1L);

        // Then
        verify(movementRepository, times(1)).existsById(1L);
        verify(movementRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMovement_WhenNotExists_ShouldThrowException() {
        // Given
        when(movementRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> movementService.deleteMovement(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Movement not found with id: '999'");

        verify(movementRepository, times(1)).existsById(999L);
        verify(movementRepository, never()).deleteById(any());
    }
}
