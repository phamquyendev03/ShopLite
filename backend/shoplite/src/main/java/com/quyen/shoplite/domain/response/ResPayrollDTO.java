package com.quyen.shoplite.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResPayrollDTO {
    private Integer id;

    // --- Employee info ---
    private Integer employeeId;
    private String employeeUsername;

    /** Kỳ lương (đầu tháng) */
    private LocalDate period;

    private Double salaryRate;
    private Double totalHours;
    private Double bonus;
    private Double penalty;

    /**
     * Tổng lương: totalHours * salaryRate + bonus - penalty
     */
    private Double totalSalary;
}
