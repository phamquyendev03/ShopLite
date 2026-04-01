package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.request.ReqProductDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ResProductDTO create(ReqProductDTO req) {
        if (productRepository.existsBySku(req.getSku())) {
            throw new IdInvalidException("SKU '" + req.getSku() + "' đã tồn tại");
        }
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category id=" + req.getCategoryId()));

        Product product = Product.builder()
                .category(category)
                .name(req.getName())
                .sku(req.getSku())
                .stock(req.getStock())
                .price(req.getPrice())
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .build();
        return DTOMapper.toResProductDTO(productRepository.save(product));
    }

    public ResProductDTO findById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
        return DTOMapper.toResProductDTO(product);
    }

    public List<ResProductDTO> findAll() {
        return productRepository.findAllByIsDeletedFalse().stream()
                .map(DTOMapper::toResProductDTO)
                .toList();
    }

    public ResProductDTO update(Integer id, ReqProductDTO req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Category id=" + req.getCategoryId()));
            product.setCategory(category);
        }
        if (req.getName() != null) product.setName(req.getName());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getStock() != null) product.setStock(req.getStock());
        return DTOMapper.toResProductDTO(productRepository.save(product));
    }

    public void softDelete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    // Dùng nội bộ (OrderService)
    public Product findEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + id));
    }
}
