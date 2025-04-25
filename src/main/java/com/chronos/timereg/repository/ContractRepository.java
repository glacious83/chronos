package com.chronos.timereg.repository;

import com.chronos.timereg.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByUser_Id(Long userId);

    @Query("SELECT c FROM Contract c WHERE " +
            "c.contractEndDate IS NULL OR " +
            "c.contractStartDate IS NULL OR " +
            "c.daysHomePerWeek IS NULL OR c.daysHomePerWeek = 0 OR " +
            "c.daysOfficePerWeek IS NULL OR c.daysOfficePerWeek = 0 OR " +
            "c.user IS NULL OR " +
            "c.workingHoursStart IS NULL OR " +
            "c.workingHoursEnd IS NULL")
    List<Contract> findContractsWithMissingFields();

    void deleteByUser_Id(Long id);
}
