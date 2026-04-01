package com.quyen.shoplite.domain;

import com.quyen.shoplite.util.constant.TypeTransactionEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "bank_code", length = 50)
    private String bankCode;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTransactionEnum type;

    @Column(length = 500)
    private String content;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
