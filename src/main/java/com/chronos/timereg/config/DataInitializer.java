package com.chronos.timereg.config;

import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if a default admin user exists (by email for example)
            if (userRepository.findByEmail("admin@nbg.gr")==null) {
                User admin = new User();
                admin.setFirstName("Default");
                admin.setLastName("Admin");
                admin.setEmail("admin@nbg.gr");
                admin.setEmployeeId("E00000");
                // It is important to store the password in hashed form
                admin.setPassword(passwordEncoder.encode("adminpass"));
                // Set admin role or title. Adjust based on your implementation.
                admin.setTitle("ADMIN");
                // Enable login by setting active and approved flags to true.
                admin.setActive(true);
                admin.setApproved(true);
                userRepository.save(admin);
            }
        };
    }
}
