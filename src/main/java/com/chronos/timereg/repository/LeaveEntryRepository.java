package com.chronos.timereg.repository;

import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LeaveEntryRepository extends JpaRepository<LeaveEntry, Long> {
    // Returns leave entries for a user between two dates.
    List<LeaveEntry> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    List<LeaveEntry> findByUser_IdAndDate(Long userId, LocalDate date);

    List<LeaveEntry> findByUser_ResponsibleManagerId(Long managerId);

    int countByUser_IdAndLeaveStatusAndDateBetween(Long userId, LeaveStatus leaveStatus, LocalDate of, LocalDate of1);
}
