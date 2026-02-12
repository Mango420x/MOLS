package com.mls.logistics.service;

import com.mls.logistics.domain.Resource;
import com.mls.logistics.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Resource-related business operations.
 * 
 * This class acts as an intermediary between controllers and repositories,
 * enforcing business rules and application logic.
 */
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    /**
     * Constructor-based dependency injection.
     * This is the recommended approach in Spring.
     */
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    /**
     * Retrieves all registered resources.
     */
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * Retrieves a resource by its identifier.
     */
    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    /**
     * Creates a new resource.
     * 
     * Business rules can be added here in the future
     * (e.g. resource availability checks).
     */
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }
}
