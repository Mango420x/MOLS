package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Resource;
import com.mls.logistics.domain.Stock;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateStockRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.StockService;
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
 * Integration tests for StockController.
 *
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private StockService stockService;

    private Stock testStock;

    @BeforeEach
    void setUp() {
        Resource resource = new Resource();
        resource.setId(1L);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        testStock = new Stock();
        testStock.setId(1L);
        testStock.setResource(resource);
        testStock.setWarehouse(warehouse);
        testStock.setQuantity(20);
    }

    @Test
    void getAllStocks_ShouldReturnStocksList() throws Exception {
        // Given
        when(stockService.getAllStocks()).thenReturn(Arrays.asList(testStock));

        // When & Then
        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(20));

        verify(stockService, times(1)).getAllStocks();
    }

    @Test
    void getStockById_WhenExists_ShouldReturnStock() throws Exception {
        // Given
        when(stockService.getStockById(1L)).thenReturn(Optional.of(testStock));

        // When & Then
        mockMvc.perform(get("/api/stocks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(20));

        verify(stockService, times(1)).getStockById(1L);
    }

    @Test
    void getStockById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(stockService.getStockById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/stocks/999"))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).getStockById(999L);
    }

    @Test
    void createStock_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateStockRequest request = new CreateStockRequest(1L, 1L, 20);
        when(stockService.createStock(any(CreateStockRequest.class))).thenReturn(testStock);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(20));

        verify(stockService, times(1)).createStock(any(CreateStockRequest.class));
    }

    @Test
    void createStock_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given
        CreateStockRequest request = new CreateStockRequest(null, null, 0);

        // When & Then
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stockService, never()).createStock(any(CreateStockRequest.class));
    }

    @Test
    void deleteStock_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(stockService).deleteStock(1L);

        // When & Then
        mockMvc.perform(delete("/api/stocks/1"))
                .andExpect(status().isNoContent());

        verify(stockService, times(1)).deleteStock(1L);
    }

    @Test
    void deleteStock_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Stock", "id", 999L))
                .when(stockService).deleteStock(999L);

        // When & Then
        mockMvc.perform(delete("/api/stocks/999"))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).deleteStock(999L);
    }
}
