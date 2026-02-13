package com.mls.logistics.controller;

import com.mls.logistics.domain.Unit;
import com.mls.logistics.service.UnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Unit>> getAllUnits() {
        List<Unit> units = unitService.getAllUnits();
        return ResponseEntity.ok(units);
    }

    /**
     * Retrieves a unit by its identifier.
     *
     * GET /api/units/{id}
     *
     * @param id unit identifier
     * @return unit if found, or 404 if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable Long id) {
        return unitService
                .getUnitById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new unit.
     *
     * POST /api/units
     *
     * @param unit unit entity to create
     * @return created unit with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Unit> createUnit(@RequestBody Unit unit) {
        Unit createdUnit = unitService.createUnit(unit);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUnit);
    }
}