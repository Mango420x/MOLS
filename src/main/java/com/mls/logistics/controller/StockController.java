package com.mls.logistics.controller;

import com.mls.logistics.domain.Stock;
import com.mls.logistics.dto.request.CreateStockRequest;
import com.mls.logistics.dto.request.UpdateStockRequest;
import com.mls.logistics.dto.response.StockResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * REST controller exposing Stock-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the StockService.
 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    /**
     * Constructor-based dependency injection.
     *
     * @param stockService service layer for stock operations
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Retrieves all stock records.
     *
     * GET /api/stocks
     *
     * @return list of stock records
     */
    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stockService
                .getAllStocks()
                .stream()
                .map(StockResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stocks);
    }

    /**
     * Retrieves a stock record by its identifier.
     *
     * GET /api/stocks/{id}
     *
     * @param id stock identifier
    * @return stock if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long id) {
        Stock stock = stockService
                .getStockById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));
        return ResponseEntity.ok(StockResponse.from(stock));
    }

    /**
     * Creates a new stock record.
     *
     * POST /api/stocks
     *
     * @param request DTO containing stock data
     * @return created stock with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<StockResponse> createStock(@Valid @RequestBody CreateStockRequest request) {
        Stock createdStock = stockService.createStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(StockResponse.from(createdStock));
    }

    /**
     * Updates an existing stock record.
     *
     * PUT /api/stocks/{id}
     *
     * @param id stock identifier
     * @param request update data
     * @return updated stock with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockResponse> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStockRequest request) {
        Stock updatedStock = stockService.updateStock(id, request);
        return ResponseEntity.ok(StockResponse.from(updatedStock));
    }

    /**
     * Deletes a stock record.
     *
     * DELETE /api/stocks/{id}
     *
     * @param id stock identifier
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}