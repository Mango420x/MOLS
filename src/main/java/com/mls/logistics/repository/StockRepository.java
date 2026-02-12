package com.mls.logistics.repository;

import com.mls.logistics.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Stock data from the database.
 */
public interface StockRepository extends JpaRepository<Stock, Long> {
    // Custom queries will be added later if needed
}
