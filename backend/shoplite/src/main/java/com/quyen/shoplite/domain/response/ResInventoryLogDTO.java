package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.TypeInventoryEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResInventoryLogDTO {
    private Integer id;
    private Integer quantityIn;
    private Integer quantityOut;
    private Integer balanceAfter;
    private Integer currentStock;
    private TypeInventoryEnum type;
    private LocalDateTime createdAt;
    private Integer productId;
    private String productName;
    private String productSku;
}
