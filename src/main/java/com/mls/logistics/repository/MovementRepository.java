package com.mls.logistics.repository;

import com.mls.logistics.domain.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Repository for accessing Movement data from the database.
 */
public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByOrderId(Long orderId, Sort sort);

    List<Movement> findByShipmentId(Long shipmentId, Sort sort);
}
