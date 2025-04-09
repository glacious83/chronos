package com.chronos.timereg.repository;

import com.chronos.timereg.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findByUserTitle(String userTitle);
}
