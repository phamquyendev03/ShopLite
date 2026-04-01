package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqProductDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.service.ProductService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ApiMessage("Tạo sản phẩm thành công")
    public ResponseEntity<ResProductDTO> create(@RequestBody ReqProductDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin sản phẩm")
    public ResponseEntity<ResProductDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    @ApiMessage("Danh sách sản phẩm")
    public ResponseEntity<List<ResProductDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật sản phẩm thành công")
    public ResponseEntity<ResProductDTO> update(@PathVariable Integer id, @RequestBody ReqProductDTO req) {
        return ResponseEntity.ok(productService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xoá sản phẩm thành công (soft delete)")
    public ResponseEntity<Void> softDelete(@PathVariable Integer id) {
        productService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
