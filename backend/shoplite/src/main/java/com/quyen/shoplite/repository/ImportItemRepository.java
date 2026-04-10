package com.quyen.shoplite.repository;

import com.quyen.shoplite.domain.ImportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportItemRepository extends JpaRepository<ImportItem, Integer> {
    List<ImportItem> findByImportOrder_Id(Integer importOrderId);
}
