package com.mls.logistics.controller;

import com.mls.logistics.domain.Movement;
import com.mls.logistics.service.MovementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing Movement-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the MovementService.
 */
@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final MovementService movementService;

    /**
     * Constructor-based dependency injection.
     *
     * @param movementService service layer for movement operations
     */
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    /**
     * Retrieves all movement records.
     *
     * GET /api/movements
     *
     * @return list of movement records
     */
    @GetMapping
    public ResponseEntity<List<Movement>> getAllMovements() {
        List<Movement> movements = movementService.getAllMovements();
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves a movement record by its identifier.
     *
     * GET /api/movements/{id}
     *
     * @param id movement identifier
     * @return movement if found, or 404 if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<Movement> getMovementById(@PathVariable Long id) {
        return movementService
                .getMovementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new movement record.
     *
     * POST /api/movements
     *
     * @param movement movement entity to create
     * @return created movement with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Movement> createMovement(@RequestBody Movement movement) {
        Movement createdMovement = movementService.createMovement(movement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovement);
    }
}