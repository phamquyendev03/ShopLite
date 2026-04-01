package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResOrderItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Long quantity;
    private Double price;
    private Double totalPrice;
}
