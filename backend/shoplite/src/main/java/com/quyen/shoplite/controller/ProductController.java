package com.quyen.shoplite.controller;

import com.quyen.shoplite.domain.request.ReqProductDTO;
import com.quyen.shoplite.domain.request.ReqUpdateProductDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.domain.response.ResProductPageDTO;
import com.quyen.shoplite.service.ProductService;
import com.quyen.shoplite.util.annotation.ApiMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * POST /api/v1/products
     * Tạo sản phẩm mới. Stock mặc định = 0.
     */
    @PostMapping
    @ApiMessage("Tạo sản phẩm thành công")
    public ResponseEntity<ResProductDTO> create(@Valid @RequestBody ReqProductDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req));
    }

    /**
     * GET /api/v1/products/{id}
     * Lấy thông tin một sản phẩm theo id.
     */
    @GetMapping("/{id}")
    @ApiMessage("Lấy thông tin sản phẩm")
    public ResponseEntity<ResProductDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * GET /api/v1/products
     * Tìm kiếm sản phẩm với filter + phân trang + sắp xếp.
     *
     * @param keyword    Tìm theo tên hoặc SKU
     * @param categoryId Lọc theo category
     * @param minPrice   Giá tối thiểu
     * @param maxPrice   Giá tối đa
     * @param page       Trang (bắt đầu từ 0)
     * @param size       Số item mỗi trang (mặc định 10)
     * @param sortBy     Trường sắp xếp (mặc định: createdAt)
     * @param sortDir    Hướng sắp xếp: asc | desc (mặc định: desc)
     */
    @GetMapping
    @ApiMessage("Danh sách sản phẩm")
    public ResponseEntity<ResProductPageDTO> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        return ResponseEntity.ok(
                productService.getProducts(keyword, categoryId, minPrice, maxPrice, page, size, sortBy, sortDir)
        );
    }

    /**
     * PUT /api/v1/products/{id}
     * Cập nhật sản phẩm (name, price, category).
     * Không cho phép thay đổi stock.
     */
    @PutMapping("/{id}")
    @ApiMessage("Cập nhật sản phẩm thành công")
    public ResponseEntity<ResProductDTO> update(@PathVariable Integer id,
                                                @Valid @RequestBody ReqUpdateProductDTO req) {
        return ResponseEntity.ok(productService.update(id, req));
    }

    /**
     * DELETE /api/v1/products/{id}
     * Xóa mềm sản phẩm (is_deleted = true).
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Xoá sản phẩm thành công")
    public ResponseEntity<Void> softDelete(@PathVariable Integer id) {
        productService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
