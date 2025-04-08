package com.chronos.timereg.service;

import com.chronos.timereg.dto.DepartmentRequest;
import com.chronos.timereg.model.Department;
import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.DepartmentRepository;
import com.chronos.timereg.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                 UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    @Override
    public Department createDepartment(DepartmentRequest departmentRequest) {
        Department department = new Department();
        department.setCode(departmentRequest.getCode());

        // Lookup the manager by managerId
        User manager = userRepository.findById(departmentRequest.getManagerId())
                .orElseThrow(() -> new RuntimeException(
                        "Manager not found with id: " + departmentRequest.getManagerId()));
        department.setManager(manager);
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existing = getDepartmentById(id);
        existing.setCode(departmentRequest.getCode());

        // Lookup the new manager by managerId
        User manager = userRepository.findById(departmentRequest.getManagerId())
                .orElseThrow(() -> new RuntimeException(
                        "Manager not found with id: " + departmentRequest.getManagerId()));
        existing.setManager(manager);
        return departmentRepository.save(existing);
    }

    @Override
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
