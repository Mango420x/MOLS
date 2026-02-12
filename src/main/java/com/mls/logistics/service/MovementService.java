package com.mls.logistics.service;

import com.mls.logistics.domain.Movement;
import com.mls.logistics.repository.MovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Movement-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class MovementService {

    private final MovementRepository movementRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public MovementService(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    /**
     * Retrieves all registered movements.
     */
    public List<Movement> getAllMovements() {
        return movementRepository.findAll();
    }

    /**
     * Retrieves a movement by its identifier.
     */
    public Optional<Movement> getMovementById(Long id) {
        return movementRepository.findById(id);
    }

    /**
     * Creates a new movement.
     * 
     * Business rules can be added here in the future
     * (e.g. validation of movement types).
     */
    public Movement createMovement(Movement movement) {
        return movementRepository.save(movement);
    }
}
