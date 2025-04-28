package com.chronos.timereg.service;

import com.chronos.timereg.model.Role;
import com.chronos.timereg.model.enums.RoleName;
import com.chronos.timereg.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository repo) {
        this.roleRepository = repo;
    }

    @Override
    public Role findByName(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role create(RoleName name) {
        if (roleRepository.findByName(name).isEmpty()) {
            return roleRepository.save(new Role(null, name));
        }
        return roleRepository.findByName(name).get();
    }
}
