package com.quyen.shoplite.unit;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.request.ReqProductDTO;
import com.quyen.shoplite.domain.request.ReqUpdateProductDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.service.ProductService;
import com.quyen.shoplite.util.error.IdInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit Test - ProductService
 * Mock: ProductRepository, CategoryRepository
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category mockCategory;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id(1)
                .name("Electronics")
                .build();

        mockProduct = Product.builder()
                .id(1)
                .name("Laptop")
                .sku("LAP-001")
                .price(1500.0)
                .stock(10L)
                .category(mockCategory)
                .isDeleted(false)
                .build();
    }

    // ─── CREATE ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("✅ Tạo sản phẩm thành công")
        void create_Success() {
            ReqProductDTO req = new ReqProductDTO();
            req.setCategoryId(1);
            req.setName("Laptop");
            req.setSku("LAP-001");
            req.setPrice(1500.0);

            when(productRepository.existsBySku("LAP-001")).thenReturn(false);
            when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
            when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

            ResProductDTO result = productService.create(req);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Laptop");
            verify(productRepository).save(argThat(p -> p.getStock() == 0L));
        }

        @Test
        @DisplayName("❌ SKU đã tồn tại → ném IdInvalidException")
        void create_DuplicateSku_ThrowsException() {
            ReqProductDTO req = new ReqProductDTO();
            req.setSku("LAP-001");
            req.setCategoryId(1);
            req.setName("Laptop");
            req.setPrice(1500.0);

            when(productRepository.existsBySku("LAP-001")).thenReturn(true);

            assertThatThrownBy(() -> productService.create(req))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("LAP-001");

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ Category không tồn tại → ném IdInvalidException")
        void create_CategoryNotFound_ThrowsException() {
            ReqProductDTO req = new ReqProductDTO();
            req.setSku("NEW-001");
            req.setCategoryId(99);
            req.setName("Test");
            req.setPrice(100.0);

            when(productRepository.existsBySku("NEW-001")).thenReturn(false);
            when(categoryRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.create(req))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("✅ Stock mặc định luôn = 0 khi tạo mới")
        void create_StockAlwaysZero() {
            ReqProductDTO req = new ReqProductDTO();
            req.setSku("NEW-002");
            req.setCategoryId(1);
            req.setName("Phone");
            req.setPrice(500.0);

            when(productRepository.existsBySku(any())).thenReturn(false);
            when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            productService.create(req);

            verify(productRepository).save(argThat(p -> p.getStock() == 0L));
        }
    }

    // ─── FIND BY ID ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("✅ Tìm sản phẩm theo ID thành công")
        void findById_Success() {
            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

            ResProductDTO result = productService.findById(1);

            assertThat(result.getName()).isEqualTo("Laptop");
        }

        @Test
        @DisplayName("❌ ID không tồn tại → ném exception")
        void findById_NotFound_ThrowsException() {
            when(productRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(99))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("❌ Sản phẩm đã bị xóa mềm → ném exception")
        void findById_SoftDeleted_ThrowsException() {
            mockProduct.setDeleted(true);
            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

            assertThatThrownBy(() -> productService.findById(1))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("đã bị xóa");
        }
    }

    // ─── UPDATE ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("✅ Cập nhật name và price thành công")
        void update_Success() {
            ReqUpdateProductDTO req = new ReqUpdateProductDTO();
            req.setName("Laptop Pro");
            req.setPrice(2000.0);

            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));
            when(productRepository.save(any())).thenReturn(mockProduct);

            productService.update(1, req);

            assertThat(mockProduct.getName()).isEqualTo("Laptop Pro");
            assertThat(mockProduct.getPrice()).isEqualTo(2000.0);
        }

        @Test
        @DisplayName("❌ Cập nhật sản phẩm đã xóa → ném exception")
        void update_DeletedProduct_ThrowsException() {
            mockProduct.setDeleted(true);
            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

            assertThatThrownBy(() -> productService.update(1, new ReqUpdateProductDTO()))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("đã bị xóa");
        }

        @Test
        @DisplayName("✅ Stock KHÔNG thay đổi khi update")
        void update_StockUnchanged() {
            long originalStock = mockProduct.getStock();
            ReqUpdateProductDTO req = new ReqUpdateProductDTO();
            req.setPrice(9999.0);

            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));
            when(productRepository.save(any())).thenReturn(mockProduct);

            productService.update(1, req);

            assertThat(mockProduct.getStock()).isEqualTo(originalStock);
        }
    }

    // ─── SOFT DELETE ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("softDelete()")
    class SoftDeleteTests {

        @Test
        @DisplayName("✅ Xóa mềm sản phẩm thành công")
        void softDelete_Success() {
            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));
            when(productRepository.save(any())).thenReturn(mockProduct);

            productService.softDelete(1);

            assertThat(mockProduct.isDeleted()).isTrue();
            verify(productRepository).save(mockProduct);
        }

        @Test
        @DisplayName("❌ Xóa sản phẩm đã xóa trước đó → ném exception")
        void softDelete_AlreadyDeleted_ThrowsException() {
            mockProduct.setDeleted(true);
            when(productRepository.findById(1)).thenReturn(Optional.of(mockProduct));

            assertThatThrownBy(() -> productService.softDelete(1))
                    .isInstanceOf(IdInvalidException.class)
                    .hasMessageContaining("đã bị xóa trước đó");

            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("❌ ID không tồn tại → ném exception")
        void softDelete_NotFound_ThrowsException() {
            when(productRepository.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.softDelete(99))
                    .isInstanceOf(IdInvalidException.class);
        }
    }
}
