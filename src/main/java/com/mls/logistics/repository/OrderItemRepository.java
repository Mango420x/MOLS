package com.mls.logistics.repository;

import com.mls.logistics.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for accessing OrderItem data from the database.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Custom queries will be added later if needed
}
