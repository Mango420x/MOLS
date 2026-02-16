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

import java.util.List;

/**
 * REST controller exposing Unit-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the UnitService.
 */
@RestController
@RequestMapping("/api/units")
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
    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getUnitById(@PathVariable Long id) {
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
    @PutMapping("/{id}")
    public ResponseEntity<UnitResponse> updateUnit(
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}