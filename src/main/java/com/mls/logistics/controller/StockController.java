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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST controller exposing Stock-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the StockService.
 */
@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stocks", description = "Operations for managing stock levels")
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
    @Operation(
        summary = "List all stocks",
        description = "Returns a list of all registered stock records in the system"
    )
    @ApiResponse(responseCode = "200", description = "Stocks retrieved successfully")
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
    @Operation(
        summary = "Get stock by ID",
        description = "Returns a single stock record by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(
            @Parameter(description = "Stock identifier", example = "1")
            @PathVariable Long id) {
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
    @Operation(
        summary = "Create a stock",
        description = "Creates a new stock record and returns the created entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Stock created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
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
    @Operation(
        summary = "Update a stock",
        description = "Updates an existing stock record. Only provided fields are updated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(responseCode = "404", description = "Stock not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<StockResponse> updateStock(
            @Parameter(description = "Stock identifier", example = "1")
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
    @Operation(
        summary = "Delete a stock",
        description = "Permanently deletes a stock record from the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Stock deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Stock not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(
            @Parameter(description = "Stock identifier", example = "1")
            @PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}