package com.mls.logistics.repository;

import com.mls.logistics.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Vehicle data from the database.
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Custom queries will be added later if needed
}
