package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResProductDTO {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String sku;
    private Long stock;
    private Double price;
    private boolean isDeleted;
    private LocalDate createdAt;
}
