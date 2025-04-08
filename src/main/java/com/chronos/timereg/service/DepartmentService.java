package com.chronos.timereg.service;

import com.chronos.timereg.model.Department;
import com.chronos.timereg.dto.DepartmentRequest;
import java.util.List;

public interface DepartmentService {
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department createDepartment(DepartmentRequest departmentRequest);
    Department updateDepartment(Long id, DepartmentRequest departmentRequest);
    void deleteDepartment(Long id);
}
