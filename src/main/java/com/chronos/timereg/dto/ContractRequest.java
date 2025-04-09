package com.chronos.timereg.dto;

import com.chronos.timereg.model.enums.EmploymentType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ContractRequest {
    private Long userId;
    private EmploymentType employmentType;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private Integer maxAnnualLeave;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Integer daysOfficePerWeek;
    private Integer daysHomePerWeek;
    private Integer trialPeriodMonths;
}
