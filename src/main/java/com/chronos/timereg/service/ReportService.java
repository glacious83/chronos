package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeavesReportDTO;
import com.chronos.timereg.dto.MonthlyRecordDTO;
import com.chronos.timereg.dto.TimeReportDTO;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    // Leaves aggregated reports
    LeavesReportDTO getWeeklyLeavesReport(Long userId, LocalDate weekStart);
    LeavesReportDTO getMonthlyLeavesReport(Long userId, int year, int month);
    LeavesReportDTO getYearlyLeavesReport(Long userId, int year);

    // Time aggregated reports
    TimeReportDTO getWeeklyTimeReport(Long userId, LocalDate weekStart);
    TimeReportDTO getMonthlyTimeReport(Long userId, int year, int month);
    TimeReportDTO getYearlyTimeReport(Long userId, int year);

    List<MonthlyRecordDTO> getMonthlyRecordReport(Long userId, int year, int month);

    List<MonthlyRecordDTO> getMonthlyRecordReportForDepartment(
            String department, int year, int month
    );

}
