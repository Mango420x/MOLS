package com.mls.logistics.repository;

import com.mls.logistics.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing Order data from the database.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByStatus(String status);

    List<Order> findByStatusAndDateCreatedBefore(String status, LocalDate cutoffDate);
}
