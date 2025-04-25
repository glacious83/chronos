package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeaveEntrySummaryDTO;
import com.chronos.timereg.dto.LeavesReportDTO;
import com.chronos.timereg.dto.MonthlyRecordDTO;
import com.chronos.timereg.dto.TimeReportDTO;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.TimeEntry;
import com.chronos.timereg.model.enums.LeaveStatus;
import com.chronos.timereg.repository.LeaveEntryRepository;
import com.chronos.timereg.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Override
    public List<MonthlyRecordDTO> getMonthlyRecordReport(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        // 1) fetch raw time‐entries
        List<TimeEntry> times = timeEntryRepository
                .findByUser_IdAndDateBetween(userId, start, end);

        // 2) fetch approved leaves
        List<LeaveEntry> leaves = leaveEntryRepository
                .findByUser_IdAndDateBetween(userId, start, end).stream()
                .filter(le -> le.getLeaveStatus() == LeaveStatus.APPROVED)
                .collect(Collectors.toList());

        List<MonthlyRecordDTO> rows = new ArrayList<>();

        // map time entries → DTO
        for (TimeEntry te : times) {
            MonthlyRecordDTO dto = new MonthlyRecordDTO();
            dto.setDate(te.getDate());
            dto.setVendorName(te.getUser().getCompany().getName());
            dto.setResourceSurname(te.getUser().getLastName());
            dto.setResourceName(te.getUser().getFirstName());
            dto.setSapNumber(te.getUser().getSapId());
            dto.setProfile(te.getUser().getTitle());
            dto.setNbgItUnit(te.getUser().getDepartment().getCode());
            dto.setNbgRequestorName(te.getUser().getResponsibleManager().getLastName());
            dto.setProjectNumber(te.getProject().getName());
            dto.setProjectName(te.getProject().getDescription());
            dto.setActuals(te.getWorkedHours() + te.getOvertimeHours());
            dto.setLeave(false);
            rows.add(dto);
        }

        // map leaves → DTO (FULL=8h, HALF=4h)
        for (LeaveEntry le : leaves) {
            MonthlyRecordDTO dto = new MonthlyRecordDTO();
            dto.setDate(le.getDate());
            dto.setVendorName(le.getUser().getCompany().getName());
            dto.setResourceSurname(le.getUser().getLastName());
            dto.setResourceName(le.getUser().getFirstName());
            dto.setSapNumber(le.getUser().getSapId());
            dto.setProfile(le.getUser().getTitle());
            dto.setNbgItUnit(le.getUser().getDepartment().getCode());
            dto.setNbgRequestorName(le.getUser().getResponsibleManager().getLastName());
            dto.setProjectNumber("LEAVE");
            dto.setProjectName(le.getLeaveType().name());
            double hours = switch (le.getLeaveType()) {
                case FULL -> 8;
                case FIRST_HALF, SECOND_HALF -> 4;
                default -> 0;
            };
            dto.setActuals(hours);
            dto.setLeave(true);
            rows.add(dto);
        }

        // sort by date (optional)
        rows.sort(Comparator.comparing(MonthlyRecordDTO::getDate));
        return rows;
    }

    @Override
    public List<MonthlyRecordDTO> getMonthlyRecordReportForDepartment(
            String department, int year, int month) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();

        // fetch all time‐entries for the dept
        List<TimeEntry> times = timeEntryRepository
                .findByUser_Department_CodeAndDateBetween(department, start, end);

        // fetch all approved leaves for the dept
        List<LeaveEntry> leaves = leaveEntryRepository
                .findByUser_Department_CodeAndDateBetween(department, start, end).stream()
                .filter(le -> le.getLeaveStatus() == LeaveStatus.APPROVED)
                .collect(Collectors.toList());

        List<MonthlyRecordDTO> rows = new ArrayList<>();

        // ---- same mapping logic as before ----
        times.forEach(te -> {
            if (te.getUser()!= null && te.getProject() != null) {
                MonthlyRecordDTO dto = new MonthlyRecordDTO();
                dto.setDate(te.getDate());
                dto.setVendorName(te.getUser().getCompany().getName());
                dto.setResourceSurname(te.getUser().getLastName());
                dto.setResourceName(te.getUser().getFirstName());
                dto.setSapNumber(te.getUser().getSapId());
                dto.setProfile(te.getUser().getTitle());
                dto.setNbgItUnit(te.getUser().getDepartment().getCode());
                dto.setNbgRequestorName(te.getUser().getResponsibleManager().getLastName());
                dto.setProjectNumber(te.getProject().getDm().getCode());
                dto.setProjectName(te.getProject().getName());
                dto.setActuals(te.getWorkedHours() + te.getOvertimeHours());
                dto.setLeave(false);
                rows.add(dto);
            }
        });

        leaves.forEach(le -> {
            MonthlyRecordDTO dto = new MonthlyRecordDTO();
            dto.setDate(le.getDate());
            dto.setVendorName(le.getUser().getCompany().getName());
            dto.setResourceSurname(le.getUser().getLastName());
            dto.setResourceName(le.getUser().getFirstName());
            dto.setSapNumber(le.getUser().getSapId());
            dto.setProfile(le.getUser().getTitle());
            dto.setNbgItUnit(le.getUser().getDepartment().getCode());
            dto.setNbgRequestorName(le.getUser().getResponsibleManager().getLastName());
            dto.setProjectNumber("LEAVE");
            dto.setProjectName(le.getLeaveType().name());
            double hours = switch (le.getLeaveType()) {
                case FULL       -> 8;
                case FIRST_HALF,
                     SECOND_HALF -> 4;
                default         -> 0;
            };
            dto.setActuals(hours);
            dto.setLeave(true);
            rows.add(dto);
        });

        rows.sort(Comparator.comparing(MonthlyRecordDTO::getDate));
        return rows;
    }
}
