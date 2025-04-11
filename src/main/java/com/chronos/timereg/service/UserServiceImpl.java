package com.chronos.timereg.service;

import com.chronos.timereg.dto.UserRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Department;
import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.DepartmentRepository;
import com.chronos.timereg.repository.RateRepository;
import com.chronos.timereg.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RateRepository rateRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository, RateRepository rateRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.rateRepository = rateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
    }

    @Override
    public User createUser(UserRequest userRequest) {
        boolean validTitle = rateRepository.findByUserTitle(userRequest.getTitle()).isPresent();
        if (!validTitle) {
            throw new BusinessException("Invalid user title. Please select one of the configured titles.");
        }
        User user = new User();
        if (userRequest.getFirstName()==null || userRequest.getFirstName().isEmpty()) {
            throw new BusinessException("First name cannot be null or empty.");
        }
        user.setFirstName(userRequest.getFirstName());
        if (userRequest.getLastName()==null || userRequest.getLastName().isEmpty()) {
            throw new BusinessException("Last name cannot be null or empty.");
        }
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());
        if (userRequest.getEmail()==null || userRequest.getEmail().isEmpty()) {
            throw new BusinessException("Email cannot be null or empty.");
        }
        user.setEmail(userRequest.getEmail());
        if (userRequest.getEmployeeId()==null || userRequest.getEmployeeId().isEmpty()) {
            throw new BusinessException("Employee ID cannot be null or empty.");
        }
        user.setEmployeeId(userRequest.getEmployeeId());
        if (userRequest.getPassword()==null || userRequest.getPassword().isEmpty()) {
            throw new BusinessException("Password cannot be null or empty.");
        }
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        if (userRequest.getTitle()==null || userRequest.getTitle().isEmpty()) {
            throw new BusinessException("Title cannot be null or empty.");
        }
        user.setTitle(userRequest.getTitle());
        // Lookup the department by id

        if (userRequest.getDepartmentId()!=null) {
            Department department = departmentRepository.findById(userRequest.getDepartmentId())
                    .orElseThrow(() -> new BusinessException("Department not found with id: " + userRequest.getDepartmentId()));
            user.setDepartment(department);
        }

        // If a responsible manager is provided, look up the User and set it; otherwise, leave null.
        if (userRequest.getResponsibleManagerId() != null) {
            User manager = userRepository.findById(userRequest.getResponsibleManagerId())
                    .orElseThrow(() -> new BusinessException("Manager not found with id: " + userRequest.getResponsibleManagerId()));
            user.setResponsibleManager(manager);
        }
        user.setActive(true); // Default to active
        user.setApproved(false); // Default to not approved
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserRequest userRequest) {
        boolean validTitle = rateRepository.findByUserTitle(userRequest.getTitle()).isPresent();
        if (!validTitle) {
            throw new BusinessException("Invalid user title. Please select one of the configured titles.");
        }
        User existing = getUserById(id);
        existing.setFirstName(userRequest.getFirstName());
        existing.setLastName(userRequest.getLastName());
        existing.setMiddleName(userRequest.getMiddleName());
        existing.setEmail(userRequest.getEmail());
        existing.setEmployeeId(userRequest.getEmployeeId());
        existing.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        existing.setTitle(userRequest.getTitle());

        Department department = departmentRepository.findById(userRequest.getDepartmentId())
                .orElseThrow(() -> new BusinessException("Department not found with id: " + userRequest.getDepartmentId()));
        existing.setDepartment(department);

        if (userRequest.getResponsibleManagerId() != null) {
            User manager = userRepository.findById(userRequest.getResponsibleManagerId())
                    .orElseThrow(() -> new BusinessException("Manager not found with id: " + userRequest.getResponsibleManagerId()));
            existing.setResponsibleManager(manager);
        } else {
            existing.setResponsibleManager(null);
        }
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserByEmployeeId(String username) {
        return userRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessException("User not found with employee ID: " + username));
    }
}
