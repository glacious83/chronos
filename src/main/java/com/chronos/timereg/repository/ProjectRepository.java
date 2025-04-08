package com.chronos.timereg.repository;

import com.chronos.timereg.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
