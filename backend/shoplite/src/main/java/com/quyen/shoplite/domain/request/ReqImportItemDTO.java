package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqImportItemDTO {

    @NotNull(message = "productId không được để trống")
    private Integer productId;

    @NotNull(message = "quantity không được để trống")
    @Positive(message = "quantity phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "importPrice không được để trống")
    @Positive(message = "importPrice phải lớn hơn 0")
    private Double importPrice;
}
