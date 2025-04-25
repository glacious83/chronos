package com.chronos.timereg.repository;

import com.chronos.timereg.dto.MonthlyRecordDTO;
import java.util.List;

public interface ReportService {
    // … existing methods …

    /**
     * Pulls every time-entry and every approved leave
     * for that user/month, and flattens them into a single list.
     */
    List<MonthlyRecordDTO> getMonthlyRecordReport(Long userId, int year, int month);
}