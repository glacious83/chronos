package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeaveEntryRequest;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;

public interface LeaveEntryService {
    // Create/update basic leave entries (for employee submission)
    LeaveEntry createLeaveEntry(LeaveEntryRequest request);
    LeaveEntry updateLeaveEntry(Long id, LeaveEntryRequest request);
    LeaveEntry getLeaveEntryById(Long id);
    List<LeaveEntry> getAllLeaveEntries();
    void deleteLeaveEntry(Long id);

    // Manager-related methods:
    List<LeaveEntry> getLeavesOfSubordinates(Long managerId);
    /**
     * Updates the status of a leave entry belonging to a subordinate.
     * Business validations:
     *   - The leave must belong to a subordinate of the manager.
     *   - The current status must be either PENDING or CANCELLATION_REQUESTED.
     *   - For a PENDING leave, new status must be either APPROVED or REJECTED.
     *   - For a CANCELLATION_REQUESTED leave, new status must be either CANCELED (if manager approves cancellation)
     *     or revert to APPROVED (if manager rejects cancellation).
     */
    LeaveEntry updateSubordinateLeaveStatus(Long leaveId, LeaveStatus newStatus, Long managerId);

    // Employee cancellation:
    /**
     * Allows an employee to cancel his/her leave.
     *   - If status is PENDING, change immediately to CANCELED.
     *   - If status is APPROVED, change to CANCELLATION_REQUESTED.
     *   - Otherwise, throw a business error.
     */
    LeaveEntry cancelLeave(Long leaveId, Long userId);

    List<LeaveEntry> getLeaveEntryByUserIdAndDates(Long userId, LocalDate startDate, LocalDate endDate);
}
