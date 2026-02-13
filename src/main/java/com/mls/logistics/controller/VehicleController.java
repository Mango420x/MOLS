package com.mls.logistics.controller;

import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.dto.request.CreateVehicleRequest;
import com.mls.logistics.dto.response.VehicleResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * REST controller exposing Vehicle-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the VehicleService.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Constructor-based dependency injection.
     *
     * @param vehicleService service layer for vehicle operations
     */
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Retrieves all vehicles.
     *
     * GET /api/vehicles
     *
     * @return list of vehicles
     */
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        List<VehicleResponse> vehicles = vehicleService
                .getAllVehicles()
                .stream()
                .map(VehicleResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicles);
    }

    /**
     * Retrieves a vehicle by its identifier.
     *
     * GET /api/vehicles/{id}
     *
     * @param id vehicle identifier
    * @return vehicle if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService
                .getVehicleById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return ResponseEntity.ok(VehicleResponse.from(vehicle));
    }

    /**
     * Creates a new vehicle.
     *
     * POST /api/vehicles
     *
     * @param request DTO containing vehicle data
     * @return created vehicle with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody CreateVehicleRequest request) {
        Vehicle createdVehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(VehicleResponse.from(createdVehicle));
    }
}