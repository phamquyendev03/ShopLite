package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.request.ReqCategoryUpsertDTO;
import com.quyen.shoplite.domain.response.ResCategoryDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    // --- Success cases ---

    @Test
    void create_ShouldReturnCategory_WhenValidRequest() {
        // Arrange
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName(" Beverages ");

        when(categoryRepository.existsByName("Beverages")).thenReturn(false);
        Category savedCategory = Category.builder().id(1).name("Beverages").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        ResCategoryDTO result = categoryService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Beverages", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void update_ShouldReturnCategory_WhenValidRequest() {
        // Arrange
        Integer id = 1;
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName(" Updated Beverages ");

        Category existingCategory = Category.builder().id(id).name("Beverages").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNot("Updated Beverages", id)).thenReturn(false);
        
        Category savedCategory = Category.builder().id(id).name("Updated Beverages").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        ResCategoryDTO result = categoryService.update(id, req);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Updated Beverages", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void delete_ShouldCallDelete_WhenCategoryExists() {
        // Arrange
        Integer id = 1;
        Category existingCategory = Category.builder().id(id).name("Beverages").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));

        // Act
        categoryService.delete(id);

        // Assert
        verify(categoryRepository).delete(existingCategory);
    }

    // --- Failure cases ---

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicateName() {
        // Arrange
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName(" Beverages ");

        when(categoryRepository.existsByName("Beverages")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> categoryService.create(req));
        assertEquals("Category name already exists: Beverages", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenCategoryNotFound() {
        // Arrange
        Integer id = 99;
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName("Beverages");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.update(id, req));
        assertEquals("Category not found with id=" + id, exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_ShouldThrowBadRequest_WhenDuplicateName() {
        // Arrange
        Integer id = 1;
        ReqCategoryUpsertDTO req = new ReqCategoryUpsertDTO();
        req.setName(" Beverages ");

        Category existingCategory = Category.builder().id(id).name("Old Name").build();
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndIdNot("Beverages", id)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> categoryService.update(id, req));
        assertEquals("Category name already exists: Beverages", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void delete_ShouldThrowNotFound_WhenCategoryNotFound() {
        // Arrange
        Integer id = 99;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(id));
        assertEquals("Category not found with id=" + id, exception.getMessage());
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
