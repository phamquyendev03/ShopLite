package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.PaymentMethodEnum;
import com.quyen.shoplite.util.constant.StatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPaymentDTO {

    @NotNull(message = "orderId không được để trống")
    private Integer orderId;

    @NotNull(message = "method không được để trống")
    private PaymentMethodEnum method;

    @NotNull(message = "amount không được để trống")
    @Positive(message = "amount phải lớn hơn 0")
    private Double amount;

    private StatusEnum status = StatusEnum.PENDING;
}
