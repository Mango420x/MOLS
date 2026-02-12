package com.mls.logistics.repository;

import com.mls.logistics.domain.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Unit data from the database.
 */
public interface UnitRepository extends JpaRepository<Unit, Long> {
    // Custom queries will be added later if needed
}
