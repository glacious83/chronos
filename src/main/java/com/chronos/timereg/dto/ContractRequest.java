package com.chronos.timereg.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ContractRequest {
    private Long userId;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    // For internal employees, this must be provided; for externals, leave it null.
    private Integer maxAnnualLeave;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;  // Optional for permanent contracts
    private Integer daysOfficePerWeek;
    private Integer daysHomePerWeek;
    // For external employees, trial period in months must be provided and ≤ 3; for internal, leave as null.
    private Integer trialPeriodMonths;
}
