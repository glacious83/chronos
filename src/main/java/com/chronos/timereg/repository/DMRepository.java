package com.chronos.timereg.repository;

import com.chronos.timereg.model.DM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DMRepository extends JpaRepository<DM, Long> {
    Optional<DM> findByCode(String code);

    @Query("SELECT dm from DM dm WHERE " +
            "dm.budget IS NULL OR dm.budget = 0 OR " +
            "dm.startDate IS NULL OR " +
            "dm.endDate IS NULL")
    List<DM> findDMsWithMissingFields();
}
