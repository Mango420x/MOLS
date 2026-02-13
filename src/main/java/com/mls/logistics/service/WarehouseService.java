package com.mls.logistics.service;

import com.mls.logistics.domain.Warehouse;
import com.mls.logistics.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import com.mls.logistics.dto.request.CreateWarehouseRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Warehouse-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /**
     * Retrieves all registered warehouses.
     */
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    /**
     * Retrieves a warehouse by its identifier.
     */
    public Optional<Warehouse> getWarehouseById(Long id) {
        return warehouseRepository.findById(id);
    }

    /**
     * Creates a new warehouse.
     * 
     * Business rules can be added here in the future
     * (e.g. warehouse capacity validation).
     */
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    /**
     * Creates a new warehouse from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing warehouse data
     * @return created warehouse entity
     */
    public Warehouse createWarehouse(CreateWarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        return warehouseRepository.save(warehouse);
    }
}
