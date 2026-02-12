package com.mls.logistics.service;

import com.mls.logistics.domain.Unit;
import com.mls.logistics.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Unit-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class UnitService {

    private final UnitRepository unitRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    /**
     * Retrieves all registered units.
     */
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    /**
     * Retrieves a unit by its identifier.
     */
    public Optional<Unit> getUnitById(Long id) {
        return unitRepository.findById(id);
    }

    /**
     * Creates a new unit.
     * 
     * Business rules can be added here in the future
     * (e.g. unique name validation).
     */
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }
}
