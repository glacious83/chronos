package com.chronos.timereg.repository;

import com.chronos.timereg.model.Role;
import com.chronos.timereg.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
