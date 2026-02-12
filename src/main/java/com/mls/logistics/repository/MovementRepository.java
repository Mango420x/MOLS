package com.mls.logistics.repository;

import com.mls.logistics.domain.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Movement data from the database.
 */
public interface MovementRepository extends JpaRepository<Movement, Long> {
    // Custom queries will be added later if needed
}
