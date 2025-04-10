package com.chronos.timereg.repository;

import com.chronos.timereg.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String dmCode);

    @Query("SELECT p from Project p WHERE " +
            "p.pm IS NULL OR " +
            "p.name IS NULL OR p.name = '' OR " +
            "p.dm IS NULL OR " +
            "p.departmentResponsible IS NULL")
    List<Project> findProjectsWithMissingFields();
}
