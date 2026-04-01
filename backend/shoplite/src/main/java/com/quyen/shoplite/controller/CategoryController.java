package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqCategoryDTO;
import com.quyen.shoplite.domain.response.ResCategoryDTO;
import com.quyen.shoplite.service.CategoryService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ApiMessage("Tạo danh mục thành công")
    public ResponseEntity<ResCategoryDTO> create(@RequestBody ReqCategoryDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin danh mục")
    public ResponseEntity<ResCategoryDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping
    @ApiMessage("Danh sách danh mục")
    public ResponseEntity<List<ResCategoryDTO>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật danh mục thành công")
    public ResponseEntity<ResCategoryDTO> update(@PathVariable Integer id, @RequestBody ReqCategoryDTO req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xoá danh mục thành công")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
