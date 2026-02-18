package com.mls.logistics.service;

import com.mls.logistics.domain.Order;
import com.mls.logistics.domain.Shipment;
import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.dto.request.CreateShipmentRequest;
import com.mls.logistics.dto.request.UpdateShipmentRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.repository.ShipmentRepository;
import org.springframework.data.domain.Sort;
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
     * Retrieves all registered shipments with sorting.
     */
    public List<Shipment> getAllShipments(Sort sort) {
        return shipmentRepository.findAll(sort);
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

    /**
     * Updates an existing shipment.
     * 
     * Only non-null fields from the request are updated.
     *
     * @param id shipment identifier
     * @param request update data
     * @return updated shipment
     * @throws ResourceNotFoundException if shipment doesn't exist
     */
    @Transactional
    public Shipment updateShipment(Long id, UpdateShipmentRequest request) {
        Shipment shipment = shipmentRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));

        if (request.getOrderId() != null) {
            Order order = new Order();
            order.setId(request.getOrderId());
            shipment.setOrder(order);
        }
        if (request.getVehicleId() != null) {
            Vehicle vehicle = new Vehicle();
            vehicle.setId(request.getVehicleId());
            shipment.setVehicle(vehicle);
        }
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(request.getWarehouseId());
            shipment.setWarehouse(warehouse);
        }
        if (request.getStatus() != null) {
            shipment.setStatus(request.getStatus());
        }

        return shipmentRepository.save(shipment);
    }

    /**
     * Deletes a shipment by ID.
     *
     * @param id shipment identifier
     * @throws ResourceNotFoundException if shipment doesn't exist
     */
    @Transactional
    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shipment", "id", id);
        }
        shipmentRepository.deleteById(id);
    }
}
