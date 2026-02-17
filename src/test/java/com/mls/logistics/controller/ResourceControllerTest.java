package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Resource;
import com.mls.logistics.dto.request.CreateResourceRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.security.service.AppUserService;
import com.mls.logistics.security.service.JwtService;
import com.mls.logistics.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

/**
 * Integration tests for ResourceController.
 *
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserService appUserService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Test Resource");
        testResource.setType("Material");
        testResource.setCriticality("HIGH");
    }

    @Test
    @WithMockUser
    void getAllResources_ShouldReturnResourcesList() throws Exception {
        // Given
        when(resourceService.getAllResources()).thenReturn(Arrays.asList(testResource));

        // When & Then
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Resource"));

        verify(resourceService, times(1)).getAllResources();
    }

    @Test
    @WithMockUser
    void getResourceById_WhenExists_ShouldReturnResource() throws Exception {
        // Given
        when(resourceService.getResourceById(1L)).thenReturn(Optional.of(testResource));

        // When & Then
        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Resource"))
                .andExpect(jsonPath("$.type").value("Material"));

        verify(resourceService, times(1)).getResourceById(1L);
    }

    @Test
    @WithMockUser
    void getResourceById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(resourceService.getResourceById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound());

        verify(resourceService, times(1)).getResourceById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateResourceRequest request = new CreateResourceRequest("New Resource", "Material", "HIGH");
        when(resourceService.createResource(any(CreateResourceRequest.class))).thenReturn(testResource);

        // When & Then
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Resource"));

        verify(resourceService, times(1)).createResource(any(CreateResourceRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given
        CreateResourceRequest request = new CreateResourceRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(resourceService, never()).createResource(any(CreateResourceRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteResource_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(resourceService).deleteResource(1L);

        // When & Then
        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isNoContent());

        verify(resourceService, times(1)).deleteResource(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteResource_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Resource", "id", 999L))
                .when(resourceService).deleteResource(999L);

        // When & Then
        mockMvc.perform(delete("/api/resources/999"))
                .andExpect(status().isNotFound());

        verify(resourceService, times(1)).deleteResource(999L);
    }
}
