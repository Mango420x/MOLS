package com.mls.logistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.logistics.domain.Unit;
import com.mls.logistics.dto.request.CreateUnitRequest;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.security.service.AppUserService;
import com.mls.logistics.security.service.JwtService;
import com.mls.logistics.service.UnitService;
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
 * Integration tests for UnitController.
 *
 * Tests HTTP layer without requiring full application context.
 * Uses MockMvc to simulate HTTP requests.
 */
@WebMvcTest(UnitController.class)
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UnitService unitService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserService appUserService;

    private Unit testUnit;

    @BeforeEach
    void setUp() {
        testUnit = new Unit();
        testUnit.setId(1L);
        testUnit.setName("Test Unit");
        testUnit.setLocation("Test Location");
    }

    @Test
    @WithMockUser
    void getAllUnits_ShouldReturnUnitsList() throws Exception {
        // Given
        when(unitService.getAllUnits()).thenReturn(Arrays.asList(testUnit));

        // When & Then
        mockMvc.perform(get("/api/units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Test Unit"));

        verify(unitService, times(1)).getAllUnits();
    }

    @Test
    @WithMockUser
    void getUnitById_WhenExists_ShouldReturnUnit() throws Exception {
        // Given
        when(unitService.getUnitById(1L)).thenReturn(Optional.of(testUnit));

        // When & Then
        mockMvc.perform(get("/api/units/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Unit"))
                .andExpect(jsonPath("$.location").value("Test Location"));

        verify(unitService, times(1)).getUnitById(1L);
    }

    @Test
    @WithMockUser
    void getUnitById_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        when(unitService.getUnitById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/units/999"))
                .andExpect(status().isNotFound());

        verify(unitService, times(1)).getUnitById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUnit_WithValidRequest_ShouldReturn201() throws Exception {
        // Given
        CreateUnitRequest request = new CreateUnitRequest("New Unit", "New Location");
        when(unitService.createUnit(any(CreateUnitRequest.class))).thenReturn(testUnit);

        // When & Then
        mockMvc.perform(post("/api/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Unit"));

        verify(unitService, times(1)).createUnit(any(CreateUnitRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUnit_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given
        CreateUnitRequest request = new CreateUnitRequest("", "");

        // When & Then
        mockMvc.perform(post("/api/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(unitService, never()).createUnit(any(CreateUnitRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUnit_WhenExists_ShouldReturn204() throws Exception {
        // Given
        doNothing().when(unitService).deleteUnit(1L);

        // When & Then
        mockMvc.perform(delete("/api/units/1"))
                .andExpect(status().isNoContent());

        verify(unitService, times(1)).deleteUnit(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUnit_WhenNotExists_ShouldReturn404() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Unit", "id", 999L))
                .when(unitService).deleteUnit(999L);

        // When & Then
        mockMvc.perform(delete("/api/units/999"))
                .andExpect(status().isNotFound());

        verify(unitService, times(1)).deleteUnit(999L);
    }
}
