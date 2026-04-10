package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.request.ReqCategoryUpsertDTO;
import com.quyen.shoplite.domain.response.ResCategoryDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResCategoryDTO create(ReqCategoryUpsertDTO req) {
        String normalizedName = req.getName().trim();
        if (categoryRepository.existsByName(normalizedName)) {
            throw new BadRequestException("Category name already exists: " + normalizedName);
        }
        Category category = Category.builder()
                .name(normalizedName)
                .build();
        return DTOMapper.toResCategoryDTO(categoryRepository.save(category));
    }

    public ResCategoryDTO findById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));
        return DTOMapper.toResCategoryDTO(category);
    }

    public List<ResCategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(DTOMapper::toResCategoryDTO)
                .toList();
    }

    public ResCategoryDTO update(Integer id, ReqCategoryUpsertDTO req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));
        String normalizedName = req.getName().trim();
        if (categoryRepository.existsByNameAndIdNot(normalizedName, id)) {
            throw new BadRequestException("Category name already exists: " + normalizedName);
        }
        category.setName(normalizedName);
        return DTOMapper.toResCategoryDTO(categoryRepository.save(category));
    }

    public void delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id=" + id));
        categoryRepository.delete(category);
    }
}
