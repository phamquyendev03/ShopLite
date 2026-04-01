package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.TypeTransactionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResTransactionDTO {
    private Integer id;
    private String externalId;
    private String bankCode;
    private Double amount;
    private TypeTransactionEnum type;
    private String content;
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private Integer orderId;
    private String orderCode;
}
