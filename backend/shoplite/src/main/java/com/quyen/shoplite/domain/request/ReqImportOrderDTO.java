package com.quyen.shoplite.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqImportOrderDTO {

    @NotNull(message = "supplierId must not be null")
    private Integer supplierId;

    @NotNull(message = "items must not be null")
    @Valid
    private List<ReqImportItemDTO> items;

    private Double tax;

    private Double discount;

    private String note;
}