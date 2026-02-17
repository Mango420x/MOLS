package com.mls.logistics.controller;

import com.mls.logistics.domain.Stock;
import com.mls.logistics.dto.request.CreateStockRequest;
import com.mls.logistics.dto.request.AdjustStockRequest;
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

    /**
     * Adjusts stock quantity by a delta value.
     *
     * PATCH /api/stocks/{id}/adjust
     *
     * This is the only valid way to modify stock quantity.
     * Positive delta adds stock (ENTRY), negative delta removes stock (EXIT).
     * Every adjustment automatically generates a Movement audit record.
     *
     * @param id      stock identifier
     * @param request delta and optional reason
     * @return updated stock with HTTP 200 status
     */
    @Operation(
        summary = "Adjust stock quantity",
        description = """
            Adjusts stock by a delta value. Positive = ENTRY, Negative = EXIT.
            Automatically generates a Movement audit record.
            Stock can never go below zero.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock adjusted successfully"),
        @ApiResponse(responseCode = "404", description = "Stock not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock for this adjustment"),
        @ApiResponse(responseCode = "400", description = "Invalid delta value")
    })
    @PatchMapping("/{id}/adjust")
    public ResponseEntity<StockResponse> adjustStock(
            @Parameter(description = "Stock identifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AdjustStockRequest request) {
        Stock adjustedStock = stockService.adjustStock(id, request);
        return ResponseEntity.ok(StockResponse.from(adjustedStock));
    }
}