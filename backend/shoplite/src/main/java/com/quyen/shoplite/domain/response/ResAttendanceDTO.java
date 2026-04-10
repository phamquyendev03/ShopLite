package com.quyen.shoplite.domain.response;

import com.quyen.shoplite.util.constant.AttendanceStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResAttendanceDTO {
    private Integer id;

    // --- Employee info ---
    private Integer employeeId;
    private String employeeUsername;

    // --- Office info ---
    private Integer officeId;
    private String officeName;

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double hours;
    private LocalDate workingDay;

    // --- GPS ---
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double distance;
    private Long lateMinutes;

    private AttendanceStatusEnum status;
}
