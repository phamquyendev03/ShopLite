package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Category;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.Unit;
import com.quyen.shoplite.domain.request.ReqProductUpsertDTO;
import com.quyen.shoplite.domain.response.ResProductDTO;
import com.quyen.shoplite.repository.CategoryRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.repository.UnitRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private ProductService productService;

    // --- Success cases ---

    @Test
    void create_ShouldReturnProduct_WhenValidRequest() {
        // Arrange
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setName(" Coke ");
        req.setSku(" SKU-123 ");
        req.setBarcode(123456789L);
        req.setCategoryId(1);
        req.setUnitId(2);
        req.setStock(100);
        req.setPrice(15.0);

        when(productRepository.existsBySku("SKU-123")).thenReturn(false);
        when(productRepository.existsByBarcode(123456789L)).thenReturn(false);
        
        Category category = Category.builder().id(1).build();
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        
        Unit unit = Unit.builder().id(2).build();
        when(unitRepository.findById(2)).thenReturn(Optional.of(unit));

        Product savedProduct = Product.builder()
                .id(10)
                .name("Coke")
                .sku("SKU-123")
                .barcode(123456789L)
                .category(category)
                .unit(unit)
                .stock(100)
                .price(15.0)
                .isDeleted(false)
                .build();
        
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ResProductDTO result = productService.create(req);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Coke", result.getName());
        assertEquals("SKU-123", result.getSku());
        assertEquals(123456789L, result.getBarcode());
        assertEquals(1, result.getCategoryId());
        assertEquals(2, result.getUnitId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_ShouldReturnProduct_WhenValidRequest() {
        // Arrange
        Integer id = 10;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setName(" Updated Coke ");
        req.setSku(" SKU-999 ");
        req.setBarcode(987654321L);
        req.setCategoryId(1);
        req.setUnitId(2);
        req.setStock(200);
        req.setPrice(20.0);

        Product existingProduct = Product.builder().id(id).isDeleted(false).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        
        when(productRepository.existsBySkuAndIdNot("SKU-999", id)).thenReturn(false);
        when(productRepository.existsByBarcodeAndIdNot(987654321L, id)).thenReturn(false);

        Category category = Category.builder().id(1).build();
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        
        Unit unit = Unit.builder().id(2).build();
        when(unitRepository.findById(2)).thenReturn(Optional.of(unit));

        Product savedProduct = Product.builder()
                .id(id)
                .name("Updated Coke")
                .sku("SKU-999")
                .barcode(987654321L)
                .category(category)
                .unit(unit)
                .stock(200)
                .price(20.0)
                .isDeleted(false)
                .build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        ResProductDTO result = productService.update(id, req);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Updated Coke", result.getName());
        assertEquals("SKU-999", result.getSku());
        assertEquals(987654321L, result.getBarcode());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void softDelete_ShouldSetIsDeletedTrue_WhenProductExists() {
        // Arrange
        Integer id = 10;
        Product existingProduct = Product.builder().id(id).isDeleted(false).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));

        // Act
        productService.softDelete(id);

        // Assert
        assertTrue(existingProduct.isDeleted());
        verify(productRepository).save(existingProduct);
    }

    // --- Failure cases ---

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicateSku() {
        // Arrange
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku(" SKU-123 ");
        when(productRepository.existsBySku("SKU-123")).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productService.create(req));
        assertEquals("SKU already exists: SKU-123", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicateBarcode() {
        // Arrange
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku("SKU-123");
        req.setBarcode(123456789L);
        when(productRepository.existsBySku("SKU-123")).thenReturn(false);
        when(productRepository.existsByBarcode(123456789L)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productService.create(req));
        assertEquals("Barcode already exists: 123456789", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowNotFound_WhenCategoryNotFound() {
        // Arrange
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku("SKU-123");
        req.setCategoryId(99);
        when(productRepository.existsBySku("SKU-123")).thenReturn(false);
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.create(req));
        assertEquals("Category not found with id=99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowNotFound_WhenUnitNotFound() {
        // Arrange
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku("SKU-123");
        req.setCategoryId(1);
        req.setUnitId(99);

        when(productRepository.existsBySku("SKU-123")).thenReturn(false);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(Category.builder().id(1).build()));
        when(unitRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.create(req));
        assertEquals("Unit not found with id=99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenProductNotFound() {
        // Arrange
        Integer id = 99;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.update(id, req));
        assertEquals("Product not found with id=99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenProductIsDeleted() {
        // Arrange
        Integer id = 10;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        Product deletedProduct = Product.builder().id(id).isDeleted(true).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(deletedProduct));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.update(id, req));
        assertEquals("Product not found with id=10", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowBadRequest_WhenDuplicateSku() {
        // Arrange
        Integer id = 10;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku(" SKU-999 ");

        Product existingProduct = Product.builder().id(id).isDeleted(false).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySkuAndIdNot("SKU-999", id)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productService.update(id, req));
        assertEquals("SKU already exists: SKU-999", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowBadRequest_WhenDuplicateBarcode() {
        // Arrange
        Integer id = 10;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku("SKU-999");
        req.setBarcode(987654321L);

        Product existingProduct = Product.builder().id(id).isDeleted(false).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySkuAndIdNot("SKU-999", id)).thenReturn(false);
        when(productRepository.existsByBarcodeAndIdNot(987654321L, id)).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> productService.update(id, req));
        assertEquals("Barcode already exists: 987654321", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowNotFound_WhenCategoryNotFound() {
        // Arrange
        Integer id = 10;
        ReqProductUpsertDTO req = new ReqProductUpsertDTO();
        req.setSku("SKU-999");
        req.setCategoryId(99);

        Product existingProduct = Product.builder().id(id).isDeleted(false).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySkuAndIdNot("SKU-999", id)).thenReturn(false);
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.update(id, req));
        assertEquals("Category not found with id=99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void softDelete_ShouldThrowNotFound_WhenProductNotFound() {
        // Arrange
        Integer id = 99;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.softDelete(id));
        assertEquals("Product not found with id=99", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void softDelete_ShouldThrowNotFound_WhenProductIsDeleted() {
        // Arrange
        Integer id = 10;
        Product deletedProduct = Product.builder().id(id).isDeleted(true).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(deletedProduct));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.softDelete(id));
        assertEquals("Product not found with id=10", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }
}
