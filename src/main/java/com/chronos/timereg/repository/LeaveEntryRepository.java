package com.chronos.timereg.repository;

import com.chronos.timereg.model.LeaveEntry;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveEntryRepository extends JpaRepository<LeaveEntry, Long> {
    // Returns leave entries for a user between two dates.
    List<LeaveEntry> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    List<LeaveEntry> findByUser_Id(Long userId);

    List<LeaveEntry> findByUser_IdAndDate(Long userId, LocalDate date);

    List<LeaveEntry> findByUser_ResponsibleManagerId(Long managerId);
}
