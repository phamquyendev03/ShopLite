package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {
    private Integer userId;
    private String customerName;
    private Double discount;
    private PaymentMethodEnum paymentMethod;
    private List<ReqOrderItemDTO> items;
}
