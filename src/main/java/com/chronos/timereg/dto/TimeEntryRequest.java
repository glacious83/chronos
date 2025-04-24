package com.chronos.timereg.dto;

import com.chronos.timereg.model.enums.WorkLocation;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating a TimeEntry.
 * Notice that leave-related fields (and system-calculated fields like compensationHours) are omitted.
 */
@Data
public class TimeEntryRequest {

    @NotNull
    private Long userId;  // The ID of the user submitting the time entry

    @NotNull
    private Long projectId;

    @NotNull
    private LocalDate date;  // Date for the time entry

    @NotNull
    @PositiveOrZero
    @DecimalMax(value = "8.0", message = "Worked hours cannot exceed 8 hours per day")
    private double workedHours;  // Actual hours worked – must not exceed 8.0 hours

    // Optional overtime hours (separate approval may be required for these)
    private double overtimeHours = 0.0;

    @NotNull
    private WorkLocation workLocation; // OFFICE or HOME
}
