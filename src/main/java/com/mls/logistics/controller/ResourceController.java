package com.mls.logistics.controller;

import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateResourceRequest;
import com.mls.logistics.dto.response.ResourceResponse;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        List<ResourceResponse> resources = resourceService
                .getAllResources()
                .stream()
                .map(ResourceResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves a resource by its identifier.
     *
     * GET /api/resources/{id}
     *
     * @param id resource identifier
    * @return resource if found; otherwise ResourceNotFoundException is thrown and translated to 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Long id) {
        Resource resource = resourceService
                .getResourceById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));
        return ResponseEntity.ok(ResourceResponse.from(resource));
    }

    /**
     * Creates a new resource.
     *
     * POST /api/resources
     *
     * @param request DTO containing resource data
     * @return created resource with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody CreateResourceRequest request) {
        Resource createdResource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResourceResponse.from(createdResource));
    }
}