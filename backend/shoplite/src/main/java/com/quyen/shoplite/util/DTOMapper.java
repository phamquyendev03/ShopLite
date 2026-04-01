package com.quyen.shoplite.util;

import com.quyen.shoplite.domain.*;
import com.quyen.shoplite.domain.response.*;

/**
 * Utility class để chuyển đổi (mapping) giữa Entity và DTO.
 * Theo cấu trúc dự án JobHunter.
 */
public class DTOMapper {

    private DTOMapper() {
    }

    // ==================== User ====================
    public static ResUserDTO toResUserDTO(User user) {
        if (user == null) return null;
        ResUserDTO dto = new ResUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // ==================== Category ====================
    public static ResCategoryDTO toResCategoryDTO(Category category) {
        if (category == null) return null;
        ResCategoryDTO dto = new ResCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    // ==================== Product ====================
    public static ResProductDTO toResProductDTO(Product product) {
        if (product == null) return null;
        ResProductDTO dto = new ResProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setStock(product.getStock());
        dto.setPrice(product.getPrice());
        dto.setDeleted(product.isDeleted());
        dto.setCreatedAt(product.getCreatedAt());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }

    // ==================== OrderItems ====================
    public static ResOrderItemDTO toResOrderItemDTO(OrderItems item) {
        if (item == null) return null;
        ResOrderItemDTO dto = new ResOrderItemDTO();
        dto.setId(item.getId());
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotalPrice(item.getTotalPrice());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
        }
        return dto;
    }

    // ==================== Order ====================
    public static ResOrderDTO toResOrderDTO(Order order) {
        if (order == null) return null;
        ResOrderDTO dto = new ResOrderDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setCustomerName(order.getCustomerName());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscount(order.getDiscount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaidAt(order.getPaidAt());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUsername(order.getUser().getUsername());
        }
        return dto;
    }

    // ==================== Transaction ====================
    public static ResTransactionDTO toResTransactionDTO(Transaction transaction) {
        if (transaction == null) return null;
        ResTransactionDTO dto = new ResTransactionDTO();
        dto.setId(transaction.getId());
        dto.setExternalId(transaction.getExternalId());
        dto.setBankCode(transaction.getBankCode());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType());
        dto.setContent(transaction.getContent());
        dto.setTransactionTime(transaction.getTransactionTime());
        dto.setCreatedAt(transaction.getCreatedAt());
        if (transaction.getOrder() != null) {
            dto.setOrderId(transaction.getOrder().getId());
            dto.setOrderCode(transaction.getOrder().getCode());
        }
        return dto;
    }

    // ==================== InventoryLogs ====================
    public static ResInventoryLogDTO toResInventoryLogDTO(InventoryLogs log) {
        if (log == null) return null;
        ResInventoryLogDTO dto = new ResInventoryLogDTO();
        dto.setId(log.getId());
        dto.setChangeQuantity(log.getChangeQuantity());
        dto.setType(log.getType());
        dto.setReferenceId(log.getReferenceId());
        dto.setCreatedAt(log.getCreatedAt());
        if (log.getProduct() != null) {
            dto.setProductId(log.getProduct().getId());
            dto.setProductName(log.getProduct().getName());
            dto.setProductSku(log.getProduct().getSku());
        }
        return dto;
    }
}
