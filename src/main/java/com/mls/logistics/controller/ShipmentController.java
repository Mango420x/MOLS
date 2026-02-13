package com.mls.logistics.controller;

import com.mls.logistics.domain.Shipment;
import com.mls.logistics.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Shipment>> getAllShipments() {
        List<Shipment> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(shipments);
    }

    /**
     * Retrieves a shipment by its identifier.
     *
     * GET /api/shipments/{id}
     *
     * @param id shipment identifier
     * @return shipment if found, or 404 if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return shipmentService
                .getShipmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new shipment.
     *
     * POST /api/shipments
     *
     * @param shipment shipment entity to create
     * @return created shipment with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        Shipment createdShipment = shipmentService.createShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }
}