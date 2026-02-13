package com.mls.logistics.service;

import com.mls.logistics.domain.Vehicle;
import com.mls.logistics.dto.request.CreateVehicleRequest;
import com.mls.logistics.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Vehicle-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Retrieves all registered vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Retrieves a vehicle by its identifier.
     */
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    /**
     * Creates a new vehicle.
     * 
     * Business rules can be added here in the future
     * (e.g. vehicle maintenance checks).
     */
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /**
     * Creates a new vehicle from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing vehicle data
     * @return created vehicle entity
     */
    @Transactional
    public Vehicle createVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(request.getType());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setStatus(request.getStatus());
        return vehicleRepository.save(vehicle);
    }
}
