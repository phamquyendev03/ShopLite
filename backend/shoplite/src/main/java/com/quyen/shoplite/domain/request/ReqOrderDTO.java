package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {

    @NotNull(message = "userId không được để trống")
    private Integer userId;

    /** ID external từ hệ thống bên ngoài (tuỳ chọn) */
    private String requestId;

    /** FK tới Customer, nullable nếu khách lẻ */
    private Integer customerId;

    private Double discount;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
    private List<ReqOrderItemDTO> items;
}
