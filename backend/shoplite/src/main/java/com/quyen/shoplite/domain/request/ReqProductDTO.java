package com.quyen.shoplite.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqProductDTO {
    private Integer categoryId;
    private String name;
    private String sku;
    private Long stock;
    private Double price;
}
