package com.chronos.timereg.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class LeavesReportDTO {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private int totalLeaves;
    private List<LeaveEntrySummaryDTO> leaveEntries;
}
