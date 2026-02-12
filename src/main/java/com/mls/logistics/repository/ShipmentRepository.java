package com.mls.logistics.repository;

import com.mls.logistics.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Shipment data from the database.
 */
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    // Custom queries will be added later if needed
}
