package com.quyen.shoplite.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    /** Latitude of office location (GPS) */
    @Column(name = "office_lat", nullable = false, precision = 10, scale = 8)
    private BigDecimal officeLat;

    /** Longitude of office location (GPS) */
    @Column(name = "office_lng", nullable = false, precision = 11, scale = 8)
    private BigDecimal officeLng;

    /** Allowed radius in meters for check-in validation */
    @Column(nullable = false)
    private Integer radius;
}
