package com.chronos.timereg.repository;

import com.chronos.timereg.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    // Finds a holiday record for the given date (if one exists)
    Optional<Holiday> findByDate(LocalDate date);
}
