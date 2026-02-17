package com.mls.logistics.controller;

import com.mls.logistics.domain.Unit;
import com.mls.logistics.dto.request.CreateUnitRequest;
import com.mls.logistics.dto.request.UpdateUnitRequest;
import com.mls.logistics.dto.response.UnitResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.UnitService;
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
 * REST controller exposing Unit-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the UnitService.
 */
@RestController
@RequestMapping("/api/units")
@Tag(name = "Units", description = "Operations for managing business units")
public class UnitController {

    private final UnitService unitService;

    /**
     * Constructor-based dependency injection.
     *
     * @param unitService service layer for unit operations
     */
    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * Retrieves all units.
     *
     * GET /api/units
     *
     * @return list of units
     */
    @Operation(
        summary = "List all units",
        description = "Returns a list of all registered units in the system"
    )
    @ApiResponse(responseCode = "200", description = "Units retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAllUnits() {
        List<UnitResponse> units = unitService
                .getAllUnits()
                .stream()
                .map(UnitResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(units);
    }

    /**
     * Retrieves a unit by its identifier.
     *
     * GET /api/units/{id}
     *
     * @param id unit identifier
    * @return unit if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @Operation(
        summary = "Get unit by ID",
        description = "Returns a single unit by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unit retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(
            @Parameter(description = "Unit identifier", example = "1")
            @PathVariable Long id) {
        Unit unit = unitService
                .getUnitById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", id));
        return ResponseEntity.ok(UnitResponse.from(unit));
    }

    /**
     * Creates a new unit.
     *
     * POST /api/units
     *
     * @param request DTO containing unit data
     * @return created unit with HTTP 201 status
     */
    @Operation(
        summary = "Create a unit",
        description = "Creates a new unit and returns the created entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Unit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<UnitResponse> createUnit(@Valid @RequestBody CreateUnitRequest request) {
        Unit createdUnit = unitService.createUnit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UnitResponse.from(createdUnit));
    }

    /**
     * Updates an existing unit.
     *
     * PUT /api/units/{id}
     *
     * @param id unit identifier
     * @param request update data
     * @return updated unit with HTTP 200 status
     */
    @Operation(
        summary = "Update a unit",
        description = "Updates an existing unit. Only provided fields are updated."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unit updated successfully"),
        @ApiResponse(responseCode = "404", description = "Unit not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
            @Parameter(description = "Unit identifier", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitRequest request) {
        Unit updatedUnit = unitService.updateUnit(id, request);
        return ResponseEntity.ok(UnitResponse.from(updatedUnit));
    }

    /**
     * Deletes a unit.
     *
     * DELETE /api/units/{id}
     *
     * @param id unit identifier
     * @return 204 No Content on success
     */
    @Operation(
        summary = "Delete a unit",
        description = "Permanently deletes a unit from the system"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Unit deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(
            @Parameter(description = "Unit identifier", example = "1")
            @PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}