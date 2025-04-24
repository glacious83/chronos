package com.chronos.timereg.service;

import com.chronos.timereg.dto.TimeEntryRequest;
import com.chronos.timereg.model.TimeEntry;

import java.time.LocalDate;
import java.util.List;

public interface TimeEntryService {

    /**
     * Creates a new TimeEntry from the given DTO.
     * Validates that total hours per day for the user do not exceed 8,
     * and that the user is not on leave for that date.
     */
    TimeEntry createTimeEntry(TimeEntryRequest timeEntryRequest);

    /**
     * Updates an existing TimeEntry (by ID) from the given DTO,
     * applying similar validations as in createTimeEntry.
     */
    TimeEntry updateTimeEntry(Long id, TimeEntryRequest timeEntryRequest);

    /**
     * Retrieves a TimeEntry by its ID.
     */
    TimeEntry getTimeEntryById(Long id);

    /**
     * Retrieves all TimeEntries in the system.
     */
    List<TimeEntry> getAllTimeEntries();

    /**
     * Deletes the specified TimeEntry by ID.
     */
    void deleteTimeEntry(Long id);

    /**
     * Returns the total compensation hours for all TimeEntries belonging to the specified user.
     */
    double getTotalCompensationHoursForUser(Long userId);

    List<TimeEntry> getTimeEntryByUserIdAndDates(Long userId, LocalDate startDate, LocalDate endDate);
}
