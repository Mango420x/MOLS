package com.mls.logistics.controller;

import com.mls.logistics.domain.Resource;
import com.mls.logistics.service.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing Resource-related API endpoints.
 *
 * This controller is responsible only for HTTP request/response handling.
 * All business logic is delegated to the ResourceService.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Constructor-based dependency injection.
     *
     * @param resourceService service layer for resource operations
     */
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * Retrieves all resources.
     *
     * GET /api/resources
     *
     * @return list of resources
     */
    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources() {
        List<Resource> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves a resource by its identifier.
     *
     * GET /api/resources/{id}
     *
     * @param id resource identifier
     * @return resource if found, or 404 if not
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceService
                .getResourceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new resource.
     *
     * POST /api/resources
     *
     * @param resource resource entity to create
     * @return created resource with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        Resource createdResource = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
    }
}