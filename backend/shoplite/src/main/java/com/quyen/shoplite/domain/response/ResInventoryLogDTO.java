package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.TypeInventoryEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResInventoryLogDTO {
    private Integer id;
    private Integer changeQuantity;
    private TypeInventoryEnum type;
    private String referenceId;
    private LocalDateTime createdAt;
    private Integer productId;
    private String productName;
    private String productSku;
}
