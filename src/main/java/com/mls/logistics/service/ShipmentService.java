package com.mls.logistics.service;

import com.mls.logistics.domain.Shipment;
import com.mls.logistics.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Shipment-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public ShipmentService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * Retrieves all registered shipments.
     */
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    /**
     * Retrieves a shipment by its identifier.
     */
    public Optional<Shipment> getShipmentById(Long id) {
        return shipmentRepository.findById(id);
    }

    /**
     * Creates a new shipment.
     * 
     * Business rules can be added here in the future
     * (e.g. shipment tracking and status updates).
     */
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
}
