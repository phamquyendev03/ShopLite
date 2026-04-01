package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.InventoryLogs;
import com.quyen.shoplite.domain.Product;
import com.quyen.shoplite.domain.request.ReqInventoryLogDTO;
import com.quyen.shoplite.domain.response.ResInventoryLogDTO;
import com.quyen.shoplite.repository.InventoryLogsRepository;
import com.quyen.shoplite.repository.ProductRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryLogService {

    private final InventoryLogsRepository inventoryLogsRepository;
    private final ProductRepository productRepository;

    public ResInventoryLogDTO create(ReqInventoryLogDTO req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Product id=" + req.getProductId()));

        InventoryLogs log = InventoryLogs.builder()
                .product(product)
                .changeQuantity(req.getChangeQuantity())
                .type(req.getType())
                .referenceId(req.getReferenceId())
                .createdAt(LocalDateTime.now())
                .build();

        // Cập nhật tồn kho theo loại
        long newStock = product.getStock() + req.getChangeQuantity();
        if (newStock < 0) {
            throw new IdInvalidException("Tồn kho không đủ cho Product id=" + req.getProductId());
        }
        product.setStock(newStock);
        productRepository.save(product);

        return DTOMapper.toResInventoryLogDTO(inventoryLogsRepository.save(log));
    }

    public List<ResInventoryLogDTO> findAll() {
        return inventoryLogsRepository.findAll().stream()
                .map(DTOMapper::toResInventoryLogDTO)
                .toList();
    }

    public List<ResInventoryLogDTO> findByProductId(Integer productId) {
        return inventoryLogsRepository.findAllByProductId(productId).stream()
                .map(DTOMapper::toResInventoryLogDTO)
                .toList();
    }
}
