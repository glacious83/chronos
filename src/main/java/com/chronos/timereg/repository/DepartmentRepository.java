package com.chronos.timereg.repository;

import com.chronos.timereg.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String deptName);

    @Query("SELECT d FROM Department d WHERE d.manager IS NULL")
    List<Department> findDepartmentsWithMissingFields();
}
