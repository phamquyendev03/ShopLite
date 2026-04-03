package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.request.ReqProductDTO;
import com.quyen.shoplite.domain.request.ReqUpdateProductDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.domain.response.ResProductPageDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.ProductSpecification;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ─── Create ────────────────────────────────────────────────────────────────
    public ResProductDTO create(ReqProductDTO req) {
        // 1. Validate SKU unique
        if (productRepository.existsBySku(req.getSku())) {
            throw new IdInvalidException("SKU '" + req.getSku() + "' đã tồn tại");
        }

        // 2. Validate category
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category id=" + req.getCategoryId()));

        // 3. Insert product (stock = 0)
        Product product = Product.builder()
                .category(category)
                .name(req.getName())
                .sku(req.getSku())
                .stock(0L)          // stock luôn = 0 khi tạo mới
                .price(req.getPrice())
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .build();

        return DTOMapper.toResProductDTO(productRepository.save(product));
    }

    // ─── Get by ID ─────────────────────────────────────────────────────────────
    public ResProductDTO findById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
        if (product.isDeleted()) {
            throw new IdInvalidException("Sản phẩm id=" + id + " đã bị xóa");
        }
        return DTOMapper.toResProductDTO(product);
    }

    // ─── Get Products (filter + pagination + sorting) ──────────────────────────
    public ResProductPageDTO getProducts(String keyword, Integer categoryId,
                                         Double minPrice, Double maxPrice,
                                         int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = ProductSpecification.filter(keyword, categoryId, minPrice, maxPrice);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        ResProductPageDTO result = new ResProductPageDTO();
        result.setTotalElements(productPage.getTotalElements());
        result.setTotalPages(productPage.getTotalPages());
        result.setPage(page);
        result.setSize(size);
        result.setData(productPage.getContent().stream().map(DTOMapper::toResProductDTO).toList());

        return result;
    }

    // ─── Update ────────────────────────────────────────────────────────────────
    public ResProductDTO update(Integer id, ReqUpdateProductDTO req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));

        if (product.isDeleted()) {
            throw new IdInvalidException("Không thể cập nhật sản phẩm đã bị xóa");
        }

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category id=" + req.getCategoryId()));
            product.setCategory(category);
        }
        if (req.getName() != null && !req.getName().isBlank()) {
            product.setName(req.getName());
        }
        if (req.getPrice() != null) {
            product.setPrice(req.getPrice());
        }
        // ⚠️ Không cho phép đổi stock — stock chỉ thay đổi qua InventoryLog

        return DTOMapper.toResProductDTO(productRepository.save(product));
    }

    // ─── Soft Delete ───────────────────────────────────────────────────────────
    public void softDelete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
        if (product.isDeleted()) {
            throw new IdInvalidException("Sản phẩm id=" + id + " đã bị xóa trước đó");
        }
        product.setDeleted(true);
        productRepository.save(product);
    }

    // ─── Internal use (OrderService) ──────────────────────────────────────────
    public Product findEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
    }
}
