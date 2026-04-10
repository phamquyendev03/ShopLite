package com.quyen.shoplite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.request.ReqUnitUpsertDTO;
import com.quyen.shoplite.repository.UnitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class UnitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UnitRepository unitRepository;

    @Test
    @DisplayName("create unit success")
    void createUnit_Success() throws Exception {
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("UnitIT");

        mockMvc.perform(post("/api/v1/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("UnitIT"));

        assertThat(unitRepository.existsByName("UnitIT")).isTrue();
    }

    @Test
    @DisplayName("create unit validation failure")
    void createUnit_ValidationFailure() throws Exception {
        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("");

        mockMvc.perform(post("/api/v1/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("get unit by id success")
    void getUnit_Success() throws Exception {
        Unit unit = unitRepository.save(Unit.builder().name("TestUnit").build());

        mockMvc.perform(get("/api/v1/units/" + unit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(unit.getId()))
                .andExpect(jsonPath("$.data.name").value("TestUnit"));
    }

    @Test
    @DisplayName("update unit success")
    void updateUnit_Success() throws Exception {
        Unit unit = unitRepository.save(Unit.builder().name("OldUnit").build());

        ReqUnitUpsertDTO req = new ReqUnitUpsertDTO();
        req.setName("UpdatedUnit");

        mockMvc.perform(put("/api/v1/units/" + unit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("UpdatedUnit"));

        Unit updated = unitRepository.findById(unit.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("UpdatedUnit");
    }

    @Test
    @DisplayName("delete unit success")
    void deleteUnit_Success() throws Exception {
        Unit unit = unitRepository.save(Unit.builder().name("ToDelUnit").build());

        mockMvc.perform(delete("/api/v1/units/" + unit.getId()))
                .andExpect(status().isNoContent());

        assertThat(unitRepository.existsById(unit.getId())).isFalse();
    }
}
