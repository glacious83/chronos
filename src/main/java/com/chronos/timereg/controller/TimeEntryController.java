package com.chronos.timereg.controller;

import com.chronos.timereg.dto.TimeEntryRequest;
import com.chronos.timereg.model.TimeEntry;
import com.chronos.timereg.service.TimeEntryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/time-entries")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> createTimeEntry(@Valid @RequestBody TimeEntryRequest timeEntryRequest) {
        TimeEntry savedEntry = timeEntryService.createTimeEntry(timeEntryRequest);
        System.out.println("Created TimeEntry with ID: " + savedEntry.getId());
        return ResponseEntity.ok(savedEntry);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> getTimeEntryById(@PathVariable Long id) {
        TimeEntry entry = timeEntryService.getTimeEntryById(id);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("{userId}/time-entries")
    public ResponseEntity<List<TimeEntry>> getTimeEntries(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso=DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso=DATE) LocalDate endDate
    ) {
        List<TimeEntry> entry = timeEntryService.getTimeEntryByUserIdAndDates(userId, startDate, endDate);
        return ResponseEntity.ok(entry);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> getAllTimeEntries() {
        List<TimeEntry> entries = timeEntryService.getAllTimeEntries();
        return ResponseEntity.ok(entries);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> updateTimeEntry(@PathVariable Long id,
                                                     @Valid @RequestBody TimeEntryRequest timeEntryRequest) {
        TimeEntry updatedEntry = timeEntryService.updateTimeEntry(id, timeEntryRequest);
        return ResponseEntity.ok(updatedEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeEntry(@PathVariable Long id) {
        timeEntryService.deleteTimeEntry(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint to get total compensation hours owed by a user.
    @GetMapping("/compensation/{userId}")
    public ResponseEntity<Double> getUserCompensationHours(@PathVariable Long userId) {
        double totalCompensation = timeEntryService.getTotalCompensationHoursForUser(userId);
        return ResponseEntity.ok(totalCompensation);
    }
}
