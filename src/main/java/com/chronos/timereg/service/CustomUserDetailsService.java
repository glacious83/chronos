package com.chronos.timereg.service;

import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // We use email as the username; adjust as necessary.
    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        log.info("Loading user by employeeId: {}", employeeId);
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with employeeId: " + employeeId));

        // Check active and approved flags.
        if (!user.isActive()) {
            throw new DisabledException("User account is inactive.");
        }
        if (!user.isApproved()) {
            throw new DisabledException("User account is not yet approved.");
        }

        // For simplicity, assign a default role. In the future, roles could be derived from user's properties.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singleton(authority));
    }
}
