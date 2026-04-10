package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.ImportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportOrderRepository extends JpaRepository<ImportOrder, Integer> {
    List<ImportOrder> findBySupplier_Id(Integer supplierId);
}
