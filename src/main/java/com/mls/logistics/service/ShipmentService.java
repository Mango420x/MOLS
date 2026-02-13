package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Shipment;
import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Shipment-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
@Transactional(readOnly = true)
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
    @Transactional
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    /**
     * Creates a new shipment from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing shipment data
     * @return created shipment entity
     */
    @Transactional
    public Shipment createShipment(CreateShipmentRequest request) {
        Shipment shipment = new Shipment();

        Order order = new Order();
        order.setId(request.getOrderId());

        Vehicle vehicle = new Vehicle();
        vehicle.setId(request.getVehicleId());

        Warehouse warehouse = new Warehouse();
        warehouse.setId(request.getWarehouseId());

        shipment.setOrder(order);
        shipment.setVehicle(vehicle);
        shipment.setWarehouse(warehouse);
        shipment.setStatus(request.getStatus());

        return shipmentRepository.save(shipment);
    }
}
