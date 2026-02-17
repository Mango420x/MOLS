package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateWarehouseRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.WarehouseService;
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

/**
 * Integration tests for WarehouseController.
 * 
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(WarehouseController.class)
class WarehouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private WarehouseService warehouseService;

    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testWarehouse = new Warehouse();
        testWarehouse.setId(1L);
        testWarehouse.setName("Test Warehouse");
        testWarehouse.setLocation("Test Location");
    }

    @Test
    void getAllWarehouses_ShouldReturnWarehousesList() throws Exception {
        // Given
        when(warehouseService.getAllWarehouses()).thenReturn(Arrays.asList(testWarehouse));

        // When & Then
        mockMvc.perform(get("/api/warehouses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Warehouse"));

        verify(warehouseService, times(1)).getAllWarehouses();
    }

    @Test
    void getWarehouseById_WhenExists_ShouldReturnWarehouse() throws Exception {
        // Given
        when(warehouseService.getWarehouseById(1L)).thenReturn(Optional.of(testWarehouse));

        // When & Then
        mockMvc.perform(get("/api/warehouses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Warehouse"))
                .andExpect(jsonPath("$.location").value("Test Location"));

        verify(warehouseService, times(1)).getWarehouseById(1L);
    }

    @Test
    void getWarehouseById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(warehouseService.getWarehouseById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/warehouses/999"))
                .andExpect(status().isNotFound());

        verify(warehouseService, times(1)).getWarehouseById(999L);
    }

    @Test
    void createWarehouse_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateWarehouseRequest request = new CreateWarehouseRequest("New Warehouse", "New Location");
        when(warehouseService.createWarehouse(any(CreateWarehouseRequest.class))).thenReturn(testWarehouse);

        // When & Then
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Warehouse"));

        verify(warehouseService, times(1)).createWarehouse(any(CreateWarehouseRequest.class));
    }

    @Test
    void createWarehouse_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given - empty name
        CreateWarehouseRequest request = new CreateWarehouseRequest("", "Location");

        // When & Then
        mockMvc.perform(post("/api/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(warehouseService, never()).createWarehouse(any(CreateWarehouseRequest.class));
    }

    @Test
    void deleteWarehouse_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(warehouseService).deleteWarehouse(1L);

        // When & Then
        mockMvc.perform(delete("/api/warehouses/1"))
                .andExpect(status().isNoContent());

        verify(warehouseService, times(1)).deleteWarehouse(1L);
    }

    @Test
    void deleteWarehouse_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Warehouse", "id", 999L))
                .when(warehouseService).deleteWarehouse(999L);

        // When & Then
        mockMvc.perform(delete("/api/warehouses/999"))
                .andExpect(status().isNotFound());

        verify(warehouseService, times(1)).deleteWarehouse(999L);
    }
}