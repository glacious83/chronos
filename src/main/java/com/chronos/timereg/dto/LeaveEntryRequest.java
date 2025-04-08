package com.chronos.timereg.dto;

import com.chronos.timereg.model.enums.LeaveType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveEntryRequest {

    @NotNull
    private Long userId;  // The ID of the user who is applying for leave

    @NotNull
    private LocalDate date;  // The leave date

    @NotNull
    private LeaveType leaveType;  // FULL, FIRST_HALF, or SECOND_HALF
}
