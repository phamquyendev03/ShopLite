package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO dùng để cập nhật sản phẩm (không cho phép đổi stock).
 */
@Getter
@Setter
public class ReqUpdateProductDTO {

    private Integer categoryId;
    private String name;

    @Positive(message = "price phải lớn hơn 0")
    private Double price;
}
