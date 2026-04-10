package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqInventoryAdjustmentDTO {

    @NotBlank(message = "reason không được để trống")
    private String reason;

    private String note;

    /** Danh sách sản phẩm điều chỉnh kèm số lượng thực đếm */
    private java.util.List<ReqAdjustmentItemDTO> items;
}
