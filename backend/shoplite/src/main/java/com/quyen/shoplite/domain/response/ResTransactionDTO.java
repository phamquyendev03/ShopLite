package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.TypeTransactionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResTransactionDTO {
    private Integer id;
    private Double amount;
    private TypeTransactionEnum type;
    private String content;
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private Integer orderId;
    private String orderCode;
    private Integer importOrderId;
    private Integer paymentId;
    private Integer payrollId;
}
