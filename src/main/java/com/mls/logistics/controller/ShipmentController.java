package com.mls.logistics.controller;

import com.mls.logistics.domain.Shipment;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.dto.request.UpdateShipmentRequest;
import com.mls.logistics.dto.response.ShipmentResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

import java.util.List;

/**
 * REST controller exposing Shipment-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the ShipmentService.
 */
@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    /**
     * Constructor-based dependency injection.
     *
     * @param shipmentService service layer for shipment operations
     */
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    /**
     * Retrieves all shipments.
     *
     * GET /api/shipments
     *
     * @return list of shipments
     */
    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAllShipments() {
        List<ShipmentResponse> shipments = shipmentService
                .getAllShipments()
                .stream()
                .map(ShipmentResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shipments);
    }

    /**
     * Retrieves a shipment by its identifier.
     *
     * GET /api/shipments/{id}
     *
     * @param id shipment identifier
    * @return shipment if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService
                .getShipmentById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
        return ResponseEntity.ok(ShipmentResponse.from(shipment));
    }

    /**
     * Creates a new shipment.
     *
     * POST /api/shipments
     *
     * @param request DTO containing shipment data
     * @return created shipment with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        Shipment createdShipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ShipmentResponse.from(createdShipment));
    }

    /**
     * Updates an existing shipment.
     *
     * PUT /api/shipments/{id}
     *
     * @param id shipment identifier
     * @param request update data
     * @return updated shipment with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShipmentResponse> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShipmentRequest request) {
        Shipment updatedShipment = shipmentService.updateShipment(id, request);
        return ResponseEntity.ok(ShipmentResponse.from(updatedShipment));
    }

    /**
     * Deletes a shipment.
     *
     * DELETE /api/shipments/{id}
     *
     * @param id shipment identifier
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
}