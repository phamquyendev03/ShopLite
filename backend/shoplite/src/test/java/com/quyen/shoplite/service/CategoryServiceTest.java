package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.request.ReqCategoryDTO;
import com.quyen.shoplite.domain.response.ResCategoryDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.util.error.IdInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1)
                .name("Electronics")
                .build();
    }

    @Test
    @DisplayName("✅ Tạo danh mục thành công")
    void create_Success() {
        ReqCategoryDTO req = new ReqCategoryDTO();
        req.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(mockCategory);

        ResCategoryDTO result = categoryService.create(req);

        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository).save(any());
    }

    @Test
    @DisplayName("❌ Tên danh mục đã tồn tại → ném exception")
    void create_DuplicateName_ThrowsException() {
        ReqCategoryDTO req = new ReqCategoryDTO();
        req.setName("Electronics");

        when(categoryRepository.existsByName("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(req))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("Electronics");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("✅ Lấy danh mục theo ID thành công")
    void findById_Success() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));

        ResCategoryDTO result = categoryService.findById(1);

        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("❌ ID không tồn tại → ném exception")
    void findById_NotFound_ThrowsException() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("✅ Lấy tất cả danh mục")
    void findAll_ReturnsList() {
        when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));

        List<ResCategoryDTO> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("✅ Cập nhật tên danh mục thành công")
    void update_Success() {
        ReqCategoryDTO req = new ReqCategoryDTO();
        req.setName("Smartphones");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(categoryRepository.save(any())).thenReturn(mockCategory);

        categoryService.update(1, req);

        assertThat(mockCategory.getName()).isEqualTo("Smartphones");
    }

    @Test
    @DisplayName("✅ Xóa danh mục thành công")
    void delete_Success() {
        when(categoryRepository.existsById(1)).thenReturn(true);

        categoryService.delete(1);

        verify(categoryRepository).deleteById(1);
    }

    @Test
    @DisplayName("❌ Xóa danh mục không tồn tại → ném exception")
    void delete_NotFound_ThrowsException() {
        when(categoryRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(99))
                .isInstanceOf(IdInvalidException.class)
                .hasMessageContaining("99");

        verify(categoryRepository, never()).deleteById(any());
    }
}
