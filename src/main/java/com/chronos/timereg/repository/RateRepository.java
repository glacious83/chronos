package com.chronos.timereg.repository;

import com.chronos.timereg.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByUserTitle(String userTitle);

    @Query("SELECT r FROM Rate r WHERE " +
            "r.rate IS NULL OR r.rate = 0 OR " +
            "r.userTitle IS NULL OR r.userTitle = ''")
    List<Rate> findRatesWithMissingFields();
}
