package com.quyen.shoplite.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderItemDTO {
    private Integer productId;
    private Long quantity;
    private Double price;
}
