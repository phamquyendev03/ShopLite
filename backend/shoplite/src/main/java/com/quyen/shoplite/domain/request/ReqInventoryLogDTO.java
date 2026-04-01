package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.TypeInventoryEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqInventoryLogDTO {
    private Integer changeQuantity;
    private TypeInventoryEnum type;
    private String referenceId;
    private Integer productId;
}
