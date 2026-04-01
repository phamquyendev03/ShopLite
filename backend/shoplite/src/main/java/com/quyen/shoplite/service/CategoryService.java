package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.request.ReqCategoryDTO;
import com.quyen.shoplite.domain.response.ResCategoryDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResCategoryDTO create(ReqCategoryDTO req) {
        if (categoryRepository.existsByName(req.getName())) {
            throw new IdInvalidException("Danh mục '" + req.getName() + "' đã tồn tại");
        }
        Category category = Category.builder()
                .name(req.getName())
                .build();
        return DTOMapper.toResCategoryDTO(categoryRepository.save(category));
    }

    public ResCategoryDTO findById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category với id=" + id));
        return DTOMapper.toResCategoryDTO(category);
    }

    public List<ResCategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(DTOMapper::toResCategoryDTO)
                .toList();
    }

    public ResCategoryDTO update(Integer id, ReqCategoryDTO req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category với id=" + id));
        category.setName(req.getName());
        return DTOMapper.toResCategoryDTO(categoryRepository.save(category));
    }

    public void delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy Category với id=" + id);
        }
        categoryRepository.deleteById(id);
    }
}
