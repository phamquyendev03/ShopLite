package com.quyen.shoplite.domain.request;

import com.quyen.shoplite.util.constant.TypeTransactionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReqTransactionDTO {
    private String externalId;
    private String bankCode;
    private Double amount;
    private TypeTransactionEnum type;
    private String content;
    private LocalDateTime transactionTime;
    private Integer orderId;
}
