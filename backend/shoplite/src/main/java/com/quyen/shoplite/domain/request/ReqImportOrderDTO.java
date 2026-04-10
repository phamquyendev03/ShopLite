package com.quyen.shoplite.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqImportOrderDTO {

    @NotNull(message = "supplierId không được để trống")
    private Integer supplierId;

    @NotNull(message = "items không được để trống")
    private List<ReqImportItemDTO> items;

    /** Thuế % (ví dụ 10.0 = 10%) */
    private Double tax;

    /** Chiết khấu (số tiền, không phải %) */
    private Double discount;

    private String note;
}
