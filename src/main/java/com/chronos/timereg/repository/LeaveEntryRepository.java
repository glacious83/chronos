package com.chronos.timereg.repository;

import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LeaveEntryRepository extends JpaRepository<LeaveEntry, Long> {
    List<LeaveEntry> findByUser_IdAndDate(Long userId, LocalDate date);

    List<LeaveEntry> findByUser_ResponsibleManagerId(Long managerId);

    // Derived query to count approved leaves within a given date range.
    int countByUser_IdAndLeaveStatusAndDateBetween(Long userId, LeaveStatus leaveStatus, LocalDate startDate, LocalDate endDate);

    // Optionally, if you need to return the actual leave records for a given date range:
    List<LeaveEntry> findByUser_IdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<LeaveEntry> findByUser_Department_CodeAndDateBetween(
            String departmentCode,
            LocalDate startDate,
            LocalDate endDate
    );
}
