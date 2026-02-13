package com.mls.logistics.controller;

import com.mls.logistics.domain.Stock;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
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
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Stock stock = stockService
                .getStockById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Stock", "id", id));
        return ResponseEntity.ok(stock);
    }

    /**
     * Creates a new stock record.
     *
     * POST /api/stocks
     *
     * @param stock stock entity to create
     * @return created stock with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        Stock createdStock = stockService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }
}