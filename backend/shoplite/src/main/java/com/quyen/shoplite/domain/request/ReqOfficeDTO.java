package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReqOfficeDTO {

    @NotBlank(message = "Tên văn phòng không được để trống")
    private String name;

    @NotNull(message = "officeLat không được để trống")
    private BigDecimal officeLat;

    @NotNull(message = "officeLng không được để trống")
    private BigDecimal officeLng;

    @NotNull(message = "radius không được để trống")
    @Positive(message = "radius phải lớn hơn 0")
    private Integer radius;
}
