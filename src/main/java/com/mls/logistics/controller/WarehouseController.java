package com.mls.logistics.controller;

import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mls.logistics.dto.request.CreateWarehouseRequest;
import com.mls.logistics.dto.response.WarehouseResponse;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST controller exposing Warehouse-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the WarehouseService.
 */
@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    /**
     * Constructor-based dependency injection.
     *
     * @param warehouseService service layer for warehouse operations
     */
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * Retrieves all warehouses.
     *
     * GET /api/warehouses
     *
     * @return list of warehouses
     */
    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        List<WarehouseResponse> warehouses = warehouseService
                .getAllWarehouses()
                .stream()
                .map(WarehouseResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(warehouses);
    }

    /**
     * Retrieves a warehouse by its identifier.
     *
     * GET /api/warehouses/{id}
     *
     * @param id warehouse identifier
    * @return warehouse if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        Warehouse warehouse = warehouseService
                .getWarehouseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return ResponseEntity.ok(WarehouseResponse.from(warehouse));
    }
    
    /**
     * Creates a new warehouse.
     *
     * POST /api/warehouses
     *
     * @param request DTO containing warehouse data
     * @return created warehouse with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WarehouseResponse.from(createdWarehouse));
    }
}
