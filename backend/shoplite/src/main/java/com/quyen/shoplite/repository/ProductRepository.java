package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findAllByIsDeletedFalse();
    List<Product> findAllByCategoryId(Integer categoryId);
}
