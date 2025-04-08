package com.chronos.timereg.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TimeReportDTO {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private double totalWorkedHours;
    private double totalOvertimeHours;
}
