package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.PaymentMethodEnum;
import com.quyen.shoplite.util.constant.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResOrderDTO {
    private Integer id;
    private String code;
    private String customerName;
    private Double totalAmount;
    private Double discount;
    private StatusEnum status;
    private PaymentMethodEnum paymentMethod;
    private LocalDate createdAt;
    private LocalDate paidAt;
    private Integer userId;
    private String username;
    private List<ResOrderItemDTO> items;
}
