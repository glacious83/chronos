package com.chronos.timereg.controller;

import com.chronos.timereg.dto.LeaveEntryRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.enums.LeaveStatus;
import com.chronos.timereg.service.LeaveEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveEntryService leaveEntryService;

    public LeaveController(LeaveEntryService leaveEntryService) {
        this.leaveEntryService = leaveEntryService;
    }

    // Get leaves of subordinates (manager view)
    @GetMapping("/subordinates/{managerId}")
    public ResponseEntity<List<LeaveEntry>> getSubordinateLeaves(@PathVariable Long managerId) {
        List<LeaveEntry> leaves = leaveEntryService.getLeavesOfSubordinates(managerId);
        return ResponseEntity.ok(leaves);
    }

    // Manager approves/rejects subordinate leave (or cancellation request)
    @PutMapping("/subordinates/{managerId}/{leaveId}")
    public ResponseEntity<LeaveEntry> updateSubordinateLeaveStatus(@PathVariable Long managerId,
                                                                   @PathVariable Long leaveId,
                                                                   @RequestParam String newStatus) {
        LeaveStatus status;
        try {
            status = LeaveStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status value: " + newStatus);
        }
        LeaveEntry updated = leaveEntryService.updateSubordinateLeaveStatus(leaveId, status, managerId);
        return ResponseEntity.ok(updated);
    }

    // Employee cancels his/her own leave
    @PutMapping("/{leaveId}/cancel")
    public ResponseEntity<LeaveEntry> cancelLeave(@PathVariable Long leaveId,
                                                  @RequestParam Long userId) {
        LeaveEntry updated = leaveEntryService.cancelLeave(leaveId, userId);
        return ResponseEntity.ok(updated);
    }

    // Optional endpoints to create, update, and get leave entries
    @PostMapping
    public ResponseEntity<LeaveEntry> createLeave(@Valid @RequestBody LeaveEntryRequest request) {
        LeaveEntry leave = leaveEntryService.createLeaveEntry(request);
        return ResponseEntity.ok(leave);
    }

    @PutMapping("/{leaveId}")
    public ResponseEntity<LeaveEntry> updateLeave(@PathVariable Long leaveId,
                                                  @Valid @RequestBody LeaveEntryRequest request) {
        LeaveEntry updated = leaveEntryService.updateLeaveEntry(leaveId, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveEntry> getLeave(@PathVariable Long leaveId) {
        LeaveEntry leave = leaveEntryService.getLeaveEntryById(leaveId);
        return ResponseEntity.ok(leave);
    }
}
