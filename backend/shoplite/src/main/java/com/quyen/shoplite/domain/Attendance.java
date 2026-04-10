package com.quyen.shoplite.domain;

import com.quyen.shoplite.util.constant.AttendanceStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    /** Total hours worked this session */
    @Column
    private Double hours;

    @Column(name = "working_day", nullable = false)
    private LocalDate workingDay;

    /** Check-in latitude */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /** Check-in longitude */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /** Distance from office in meters */
    @Column
    private Double distance;

    /** Minutes late compared to scheduled start */
    @Column(name = "late_minutes")
    private Long lateMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatusEnum status;
}
