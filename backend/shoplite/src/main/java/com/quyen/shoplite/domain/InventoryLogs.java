package com.quyen.shoplite.domain;

import com.quyen.shoplite.util.constant.TypeInventoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "change_quantity", nullable = false)
    private Integer changeQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeInventoryEnum type;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
