package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeaveEntrySummaryDTO;
import com.chronos.timereg.dto.LeavesReportDTO;
import com.chronos.timereg.dto.TimeReportDTO;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.TimeEntry;
import com.chronos.timereg.repository.LeaveEntryRepository;
import com.chronos.timereg.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportServiceImpl aggregates leave entries and time entries for a user
 * over weekly, monthly, and yearly periods.
 */
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final LeaveEntryRepository leaveEntryRepository;
    private final TimeEntryRepository timeEntryRepository;

    public ReportServiceImpl(LeaveEntryRepository leaveEntryRepository, TimeEntryRepository timeEntryRepository) {
        this.leaveEntryRepository = leaveEntryRepository;
        this.timeEntryRepository = timeEntryRepository;
    }

    // ----- Leaves Reports -----

    @Override
    public LeavesReportDTO getWeeklyLeavesReport(Long userId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<LeaveEntry> leaves = leaveEntryRepository.findByUser_IdAndDateBetween(userId, weekStart, weekEnd);

        List<LeaveEntrySummaryDTO> summaryList = leaves.stream().map(leave -> {
            LeaveEntrySummaryDTO dto = new LeaveEntrySummaryDTO();
            dto.setId(leave.getId());
            dto.setDate(leave.getDate());
            dto.setLeaveType(leave.getLeaveType());
            dto.setUserId(leave.getUser().getId());
            return dto;
        }).collect(Collectors.toList());

        LeavesReportDTO report = new LeavesReportDTO();
        report.setPeriodStart(weekStart);
        report.setPeriodEnd(weekEnd);
        report.setTotalLeaves(summaryList.size());
        report.setLeaveEntries(summaryList);
        return report;
    }

    @Override
    public LeavesReportDTO getMonthlyLeavesReport(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<LeaveEntry> leaves = leaveEntryRepository.findByUser_IdAndDateBetween(userId, start, end);

        List<LeaveEntrySummaryDTO> summaryList = leaves.stream().map(leave -> {
            LeaveEntrySummaryDTO dto = new LeaveEntrySummaryDTO();
            dto.setId(leave.getId());
            dto.setDate(leave.getDate());
            dto.setLeaveType(leave.getLeaveType());
            dto.setUserId(leave.getUser().getId());
            return dto;
        }).collect(Collectors.toList());

        LeavesReportDTO report = new LeavesReportDTO();
        report.setPeriodStart(start);
        report.setPeriodEnd(end);
        report.setTotalLeaves(summaryList.size());
        report.setLeaveEntries(summaryList);
        return report;
    }

    @Override
    public LeavesReportDTO getYearlyLeavesReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<LeaveEntry> leaves = leaveEntryRepository.findByUser_IdAndDateBetween(userId, start, end);

        List<LeaveEntrySummaryDTO> summaryList = leaves.stream().map(leave -> {
            LeaveEntrySummaryDTO dto = new LeaveEntrySummaryDTO();
            dto.setId(leave.getId());
            dto.setDate(leave.getDate());
            dto.setLeaveType(leave.getLeaveType());
            dto.setUserId(leave.getUser().getId());
            return dto;
        }).collect(Collectors.toList());

        LeavesReportDTO report = new LeavesReportDTO();
        report.setPeriodStart(start);
        report.setPeriodEnd(end);
        report.setTotalLeaves(summaryList.size());
        report.setLeaveEntries(summaryList);
        return report;
    }

    // ----- Time Reports -----

    @Override
    public TimeReportDTO getWeeklyTimeReport(Long userId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        List<TimeEntry> entries = timeEntryRepository.findByUser_IdAndDateBetween(userId, weekStart, weekEnd);

        double totalWorked = entries.stream().mapToDouble(TimeEntry::getWorkedHours).sum();
        double totalOvertime = entries.stream().mapToDouble(TimeEntry::getOvertimeHours).sum();

        TimeReportDTO report = new TimeReportDTO();
        report.setPeriodStart(weekStart);
        report.setPeriodEnd(weekEnd);
        report.setTotalWorkedHours(totalWorked);
        report.setTotalOvertimeHours(totalOvertime);
        return report;
    }

    @Override
    public TimeReportDTO getMonthlyTimeReport(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<TimeEntry> entries = timeEntryRepository.findByUser_IdAndDateBetween(userId, start, end);

        double totalWorked = entries.stream().mapToDouble(TimeEntry::getWorkedHours).sum();
        double totalOvertime = entries.stream().mapToDouble(TimeEntry::getOvertimeHours).sum();

        TimeReportDTO report = new TimeReportDTO();
        report.setPeriodStart(start);
        report.setPeriodEnd(end);
        report.setTotalWorkedHours(totalWorked);
        report.setTotalOvertimeHours(totalOvertime);
        return report;
    }

    @Override
    public TimeReportDTO getYearlyTimeReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<TimeEntry> entries = timeEntryRepository.findByUser_IdAndDateBetween(userId, start, end);

        double totalWorked = entries.stream().mapToDouble(TimeEntry::getWorkedHours).sum();
        double totalOvertime = entries.stream().mapToDouble(TimeEntry::getOvertimeHours).sum();

        TimeReportDTO report = new TimeReportDTO();
        report.setPeriodStart(start);
        report.setPeriodEnd(end);
        report.setTotalWorkedHours(totalWorked);
        report.setTotalOvertimeHours(totalOvertime);
        return report;
    }
}
