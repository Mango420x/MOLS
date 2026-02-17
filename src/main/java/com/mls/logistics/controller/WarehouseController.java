package com.mls.logistics.controller;

import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mls.logistics.dto.request.CreateWarehouseRequest;
import com.mls.logistics.dto.response.WarehouseResponse;
import com.mls.logistics.dto.request.UpdateWarehouseRequest;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * REST controller exposing Warehouse-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the WarehouseService.
 */
@RestController
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouses", description = "Operations for managing storage locations")
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
    @Operation(
        summary = "List all warehouses",
        description = "Returns a list of all registered warehouses in the system"
    )
    @ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully")
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
    @Operation(
        summary = "Get warehouse by ID",
        description = "Returns a single warehouse by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Warehouse retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(
            @Parameter(description = "Warehouse identifier", example = "1")
            @PathVariable Long id) {
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
    @Operation(
        summary = "Create a warehouse",
        description = "Creates a new warehouse and returns the created entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WarehouseResponse.from(createdWarehouse));
    }

    /**
     * Updates an existing warehouse.
     *
     * PUT /api/warehouses/{id}
     *
     * @param id warehouse identifier
     * @param request update data
     * @return updated warehouse
     */
    @Operation(
        summary = "Update a warehouse",
        description = "Updates an existing warehouse. Only provided fields are updated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Warehouse updated successfully"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @Parameter(description = "Warehouse identifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateWarehouseRequest request) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(WarehouseResponse.from(updatedWarehouse));
    }

    /**
     * Deletes a warehouse.
     *
     * DELETE /api/warehouses/{id}
     *
     * @param id warehouse identifier
     * @return 204 No Content on success
     */
    @Operation(
        summary = "Delete a warehouse",
        description = "Permanently deletes a warehouse from the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Warehouse deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@Parameter(description = "Warehouse identifier", example = "1")
            @PathVariable Long id) { 
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
