package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Movement;
import com.mls.logistics.domain.Stock;
import com.mls.logistics.dto.request.CreateMovementRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.MovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for MovementController.
 *
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(MovementController.class)
class MovementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
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
    void getAllMovements_ShouldReturnMovementsList() throws Exception {
        // Given
        when(movementService.getAllMovements()).thenReturn(Arrays.asList(testMovement));

        // When & Then
        mockMvc.perform(get("/api/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("IN"))
                .andExpect(jsonPath("$[0].quantity").value(5));

        verify(movementService, times(1)).getAllMovements();
    }

    @Test
    void getMovementById_WhenExists_ShouldReturnMovement() throws Exception {
        // Given
        when(movementService.getMovementById(1L)).thenReturn(Optional.of(testMovement));

        // When & Then
        mockMvc.perform(get("/api/movements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("IN"))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(movementService, times(1)).getMovementById(1L);
    }

    @Test
    void getMovementById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(movementService.getMovementById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/movements/999"))
                .andExpect(status().isNotFound());

        verify(movementService, times(1)).getMovementById(999L);
    }

    @Test
    void createMovement_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateMovementRequest request = new CreateMovementRequest(1L, "IN", 5, LocalDateTime.now());
        when(movementService.createMovement(any(CreateMovementRequest.class))).thenReturn(testMovement);

        // When & Then
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("IN"));

        verify(movementService, times(1)).createMovement(any(CreateMovementRequest.class));
    }

    @Test
    void createMovement_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given
        CreateMovementRequest request = new CreateMovementRequest(null, "", 0, null);

        // When & Then
        mockMvc.perform(post("/api/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(movementService, never()).createMovement(any(CreateMovementRequest.class));
    }

    @Test
    void deleteMovement_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(movementService).deleteMovement(1L);

        // When & Then
        mockMvc.perform(delete("/api/movements/1"))
                .andExpect(status().isNoContent());

        verify(movementService, times(1)).deleteMovement(1L);
    }

    @Test
    void deleteMovement_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Movement", "id", 999L))
                .when(movementService).deleteMovement(999L);

        // When & Then
        mockMvc.perform(delete("/api/movements/999"))
                .andExpect(status().isNotFound());

        verify(movementService, times(1)).deleteMovement(999L);
    }
}
