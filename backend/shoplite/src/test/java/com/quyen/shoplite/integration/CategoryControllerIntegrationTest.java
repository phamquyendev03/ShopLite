package com.quyen.shoplite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.request.ReqCategoryUpsertDTO;
import com.quyen.shoplite.repository.CategoryRepository;
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
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("create category success")
    void createCategory_Success() throws Exception {
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName("CategoryIT");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("CategoryIT"));

        assertThat(categoryRepository.existsByName("CategoryIT")).isTrue();
    }

    @Test
    @DisplayName("create category validation failure")
    void createCategory_ValidationFailure() throws Exception {
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName(" ");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("get category by id success")
    void getCategory_Success() throws Exception {
        Category category = categoryRepository.save(Category.builder().name("TestCat").build());

        mockMvc.perform(get("/api/v1/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(category.getId()))
                .andExpect(jsonPath("$.data.name").value("TestCat"));
    }

    @Test
    @DisplayName("get category not found")
    void getCategory_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/categories/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Category not found with id=999999"));
    }

    @Test
    @DisplayName("update category success")
    void updateCategory_Success() throws Exception {
        Category category = categoryRepository.save(Category.builder().name("OldCat").build());

        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName("UpdatedCat");

        mockMvc.perform(put("/api/v1/categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("UpdatedCat"));

        Category updated = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("UpdatedCat");
    }

    @Test
    @DisplayName("delete category success")
    void deleteCategory_Success() throws Exception {
        Category category = categoryRepository.save(Category.builder().name("ToDel").build());

        mockMvc.perform(delete("/api/v1/categories/" + category.getId()))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.existsById(category.getId())).isFalse();
    }
}
