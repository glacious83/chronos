package com.chronos.timereg.service;

import com.chronos.timereg.model.Role;
import com.chronos.timereg.model.enums.RoleName;

import java.util.List;

public interface RoleService {
    Role findByName(RoleName name);
    List<Role> findAll();
    Role create(RoleName name);
}
