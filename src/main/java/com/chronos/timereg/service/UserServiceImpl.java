package com.chronos.timereg.service;

import com.chronos.timereg.dto.UserRequest;
import com.chronos.timereg.model.Department;
import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.DepartmentRepository;
import com.chronos.timereg.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserServiceImpl(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User createUser(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setMiddleName(userRequest.getMiddleName());
        user.setEmail(userRequest.getEmail());
        user.setEmployeeId(userRequest.getEmployeeId());
        user.setPassword(userRequest.getPassword());
        user.setTitle(userRequest.getTitle());

        // Lookup the department by id
        Department department = departmentRepository.findById(userRequest.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + userRequest.getDepartmentId()));
        user.setDepartment(department);

        // If a responsible manager is provided, look up the User and set it; otherwise, leave null.
        if (userRequest.getResponsibleManagerId() != null) {
            User manager = userRepository.findById(userRequest.getResponsibleManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userRequest.getResponsibleManagerId()));
            user.setResponsibleManager(manager);
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserRequest userRequest) {
        User existing = getUserById(id);
        existing.setFirstName(userRequest.getFirstName());
        existing.setLastName(userRequest.getLastName());
        existing.setMiddleName(userRequest.getMiddleName());
        existing.setEmail(userRequest.getEmail());
        existing.setEmployeeId(userRequest.getEmployeeId());
        existing.setPassword(userRequest.getPassword());
        existing.setTitle(userRequest.getTitle());

        Department department = departmentRepository.findById(userRequest.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + userRequest.getDepartmentId()));
        existing.setDepartment(department);

        if (userRequest.getResponsibleManagerId() != null) {
            User manager = userRepository.findById(userRequest.getResponsibleManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userRequest.getResponsibleManagerId()));
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
}
