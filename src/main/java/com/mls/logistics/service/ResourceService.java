package com.mls.logistics.service;

import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateResourceRequest;
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

    /**
     * Creates a new resource from a DTO request.
     * 
     * This method separates API contracts from domain logic.
     *
     * @param request DTO containing resource data
     * @return created resource entity
     */
    public Resource createResource(CreateResourceRequest request) {
        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setCriticality(request.getCriticality());
        return resourceRepository.save(resource);
    }
}
