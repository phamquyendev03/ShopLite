package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.TypeInventoryEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqInventoryLogDTO {

    @NotNull(message = "productId không được để trống")
    private Integer productId;

    @NotNull(message = "changeQuantity không được để trống")
    private Integer changeQuantity;

    @NotNull(message = "type không được để trống")
    private TypeInventoryEnum type;

    /** FK tới InventoryAdjustment nếu đây là log từ phiên kiểm kê */
    private Integer adjustmentId;
}
