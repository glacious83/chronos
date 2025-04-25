package com.chronos.timereg.controller;

import com.chronos.timereg.dto.LeavesReportDTO;
import com.chronos.timereg.dto.MonthlyRecordDTO;
import com.chronos.timereg.dto.TimeReportDTO;
import com.chronos.timereg.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ----- Leaves Reports -----

    // Weekly leaves report. WeekStart should be provided in ISO format (YYYY-MM-DD), e.g., 2025-04-07 (Monday).
    @GetMapping("/leaves/weekly/{userId}")
    public ResponseEntity<LeavesReportDTO> getWeeklyLeavesReport(
            @PathVariable Long userId,
            @RequestParam("weekStart") String weekStart) {
        LeavesReportDTO report = reportService.getWeeklyLeavesReport(userId, LocalDate.parse(weekStart));
        return ResponseEntity.ok(report);
    }

    // Monthly leaves report.
    @GetMapping("/leaves/monthly/{userId}")
    public ResponseEntity<LeavesReportDTO> getMonthlyLeavesReport(
            @PathVariable Long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        LeavesReportDTO report = reportService.getMonthlyLeavesReport(userId, year, month);
        return ResponseEntity.ok(report);
    }

    // Yearly leaves report.
    @GetMapping("/leaves/yearly/{userId}")
    public ResponseEntity<LeavesReportDTO> getYearlyLeavesReport(
            @PathVariable Long userId,
            @RequestParam("year") int year) {
        LeavesReportDTO report = reportService.getYearlyLeavesReport(userId, year);
        return ResponseEntity.ok(report);
    }

    // ----- Time Reports -----

    // Weekly time report.
    @GetMapping("/time/weekly/{userId}")
    public ResponseEntity<TimeReportDTO> getWeeklyTimeReport(
            @PathVariable Long userId,
            @RequestParam("weekStart") String weekStart) {
        TimeReportDTO report = reportService.getWeeklyTimeReport(userId, LocalDate.parse(weekStart));
        return ResponseEntity.ok(report);
    }

    // Monthly time report.
    @GetMapping("/time/monthly/{userId}")
    public ResponseEntity<TimeReportDTO> getMonthlyTimeReport(
            @PathVariable Long userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        TimeReportDTO report = reportService.getMonthlyTimeReport(userId, year, month);
        return ResponseEntity.ok(report);
    }

    // Yearly time report.
    @GetMapping("/time/yearly/{userId}")
    public ResponseEntity<TimeReportDTO> getYearlyTimeReport(
            @PathVariable Long userId,
            @RequestParam("year") int year) {
        TimeReportDTO report = reportService.getYearlyTimeReport(userId, year);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{userId}/monthly-records")
    public List<MonthlyRecordDTO> monthlyRecords(
            @PathVariable Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportService.getMonthlyRecordReport(userId, year, month);
    }

    @GetMapping("/monthly-records")
    public List<MonthlyRecordDTO> monthlyRecordsForDepartment(
            @RequestParam String department,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportService
                .getMonthlyRecordReportForDepartment(department, year, month);
    }
}
