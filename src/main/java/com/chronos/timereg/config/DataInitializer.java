package com.chronos.timereg.config;

import com.chronos.timereg.model.User;
import com.chronos.timereg.model.enums.RoleName;
import com.chronos.timereg.service.RoleService;
import com.chronos.timereg.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            RoleService roleService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // 1) Ensure all roles exist
            for (RoleName rn : RoleName.values()) {
                roleService.create(rn);
            }

            // 2) Create default admin if missing
            if (userRepository.findByEmail("admin@nbg.gr") == null) {
                User admin = new User();
                admin.setFirstName("Default");
                admin.setLastName("Admin");
                admin.setEmail("admin@nbg.gr");
                admin.setEmployeeId("E00000");
                admin.setPassword(passwordEncoder.encode("adminpass"));
                admin.setActive(true);
                admin.setApproved(true);

                // assign roles
                admin.getRoles().add(roleService.findByName(RoleName.ROLE_GLOBAL_ADMIN));
                admin.getRoles().add(roleService.findByName(RoleName.ROLE_DIRECTORY_ADMIN));
                admin.getRoles().add(roleService.findByName(RoleName.ROLE_MANAGER));
                admin.getRoles().add(roleService.findByName(RoleName.ROLE_USER));

                userRepository.save(admin);
            }
        };
    }
}
