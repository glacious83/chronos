package com.chronos.timereg.repository;

import com.chronos.timereg.model.DM;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DMRepository extends JpaRepository<DM, Long> {
    Optional<DM> findByCode(String code);
}
