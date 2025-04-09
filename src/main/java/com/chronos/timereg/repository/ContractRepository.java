package com.chronos.timereg.repository;

import com.chronos.timereg.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}
