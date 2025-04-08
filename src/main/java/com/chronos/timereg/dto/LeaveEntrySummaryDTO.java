package com.chronos.timereg.dto;

import com.chronos.timereg.model.enums.LeaveType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LeaveEntrySummaryDTO {
    private Long id;          // The leave entry ID
    private LocalDate date;   // The date of the leave
    private LeaveType leaveType; // The type of leave (e.g., FULL, FIRST_HALF, SECOND_HALF)
    private Long userId;      // Only the user's ID, not the full User object
}
