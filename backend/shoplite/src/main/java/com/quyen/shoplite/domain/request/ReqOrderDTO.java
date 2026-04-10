package com.quyen.shoplite.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {

    @NotNull(message = "userId must not be null")
    private Integer userId;

    private String requestId;

    private Integer customerId;

    private Double discount;

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<ReqOrderItemDTO> items;
}