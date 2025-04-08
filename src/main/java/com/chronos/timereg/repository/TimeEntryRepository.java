package com.chronos.timereg.repository;

import com.chronos.timereg.model.TimeEntry;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    // Returns time entries for a user between two dates.
    List<TimeEntry> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    // Also keep existing methods:
    List<TimeEntry> findByUser_Id(Long userId);

    List<TimeEntry> findByUser_IdAndDate(@NotNull Long userId, @NotNull LocalDate date);
}
