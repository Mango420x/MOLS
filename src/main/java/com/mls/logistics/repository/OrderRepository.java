package com.mls.logistics.repository;

import com.mls.logistics.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing Order data from the database.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Custom queries will be added later if needed
}
