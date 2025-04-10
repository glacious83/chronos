package com.chronos.timereg.repository;

import com.chronos.timereg.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    Optional<WorkOrder> findByCode(String code);

    @Query("SELECT wo from WorkOrder wo WHERE " +
            "wo.budget IS NULL OR wo.budget = 0 OR " +
            "wo.code IS NULL OR wo.code ='' OR " +
            "wo.createdDate IS NULL OR " +
            "wo.deadline IS NULL OR " +
            "wo.endDate IS NULL OR " +
            "wo.pm IS NULL OR " +
            "wo.startDate IS NULL OR " +
            "wo.status IS NULL OR " +
            "wo.title IS NULL OR wo.title ='' OR " +
            "wo.company IS NULL")
    List<WorkOrder> findWorkOrdersWithMissingFields();
}
