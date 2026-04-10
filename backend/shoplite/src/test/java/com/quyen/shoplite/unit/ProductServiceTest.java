package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.request.ReqProductUpsertDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.repository.UnitRepository;
import com.quyen.shoplite.service.ProductService;
import com.quyen.shoplite.util.error.BadRequestException;
import com.quyen.shoplite.util.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private ProductService productService;

    private Category category;
    private Unit unit;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1).name("Electronics").build();
        unit = Unit.builder().id(1).name("Piece").build();
    }

    @Test
    void create_Success() {
        ReqProductUpsertDTO req = buildReq();
        Product product = Product.builder()
                .id(1)
                .category(category)
                .unit(unit)
                .name(req.getName())
                .sku(req.getSku())
                .barcode(req.getBarcode())
                .stock(req.getStock())
                .price(req.getPrice())
                .isDeleted(false)
                .build();

        when(productRepository.existsBySku("LAP-001")).thenReturn(false);
        when(productRepository.existsByBarcode(8931234567890L)).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ResProductDTO result = productService.create(req);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getCategoryId()).isEqualTo(1);
        assertThat(result.getUnitId()).isEqualTo(1);
    }

    @Test
    void create_DuplicateSku_ThrowsBadRequest() {
        ReqProductUpsertDTO req = buildReq();

        when(productRepository.existsBySku("LAP-001")).thenReturn(true);

        assertThatThrownBy(() -> productService.create(req))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("SKU");
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_CategoryNotFound_ThrowsNotFound() {
        ReqProductUpsertDTO req = buildReq();
        Product existing = Product.builder().id(1).isDeleted(false).build();

        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuAndIdNot("LAP-001", 1)).thenReturn(false);
        when(productRepository.existsByBarcodeAndIdNot(8931234567890L, 1)).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(1, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category");
    }

    @Test
    void delete_NotFound_ThrowsNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.softDelete(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private ReqProductUpsertDTO buildReq() {
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setCategoryId(1);
        req.setUnitId(1);
        req.setName("Laptop");
        req.setSku("LAP-001");
        req.setBarcode(8931234567890L);
        req.setStock(10);
        req.setPrice(1500.0);
        return req;
    }
}
