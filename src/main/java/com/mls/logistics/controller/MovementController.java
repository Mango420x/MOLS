package com.mls.logistics.controller;

import com.mls.logistics.domain.Movement;
import com.mls.logistics.dto.request.CreateMovementRequest;
import com.mls.logistics.dto.response.MovementResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.MovementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<MovementResponse>> getAllMovements() {
        List<MovementResponse> movements = movementService
                .getAllMovements()
                .stream()
                .map(MovementResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(movements);
    }

    /**
     * Retrieves a movement record by its identifier.
     *
     * GET /api/movements/{id}
     *
     * @param id movement identifier
    * @return movement if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponse> getMovementById(@PathVariable Long id) {
        Movement movement = movementService
                .getMovementById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movement", "id", id));
        return ResponseEntity.ok(MovementResponse.from(movement));
    }

    /**
     * Creates a new movement record.
     *
     * POST /api/movements
     *
     * @param request DTO containing movement data
     * @return created movement with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<MovementResponse> createMovement(@RequestBody CreateMovementRequest request) {
        Movement createdMovement = movementService.createMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(MovementResponse.from(createdMovement));
    }
}