package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderItemDTO {

    @NotNull(message = "productId must not be null")
    private Integer productId;

    @NotNull(message = "quantity must not be null")
    @Positive(message = "quantity must be greater than 0")
    private Long quantity;

    @NotNull(message = "price must not be null")
    @Positive(message = "price must be greater than 0")
    private Double price;
}