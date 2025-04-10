package com.chronos.timereg.service;

import com.chronos.timereg.dto.AdminErrorDTO;
import com.chronos.timereg.model.*;
import com.chronos.timereg.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final RateRepository rateRepository;
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final DepartmentRepository departmentRepository;
    private final DMRepository dmRepository;
    private final LocationRepository locationRepository;
    private final ProjectRepository projectRepository;
    private final WorkOrderRepository workOrderRepository;


    public AdminServiceImpl(RateRepository rateRepository, HolidayRepository holidayRepository, UserRepository userRepository, ContractRepository contractRepository, DepartmentRepository departmentRepository, DMRepository dmRepository, LocationRepository locationRepository, ProjectRepository projectRepository, WorkOrderRepository workOrderRepository) {
        this.rateRepository = rateRepository;
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.departmentRepository = departmentRepository;
        this.dmRepository = dmRepository;
        this.locationRepository = locationRepository;
        this.projectRepository = projectRepository;
        this.workOrderRepository = workOrderRepository;
    }


    @Override
    public AdminErrorDTO checkErrors() {
        AdminErrorDTO adminErrorDTO = new AdminErrorDTO();
        List<Rate> rateError;
        boolean holidayError;
        List<User> userError;
        List<Contract> contractError;
        List<Department> departmentError;
        List<DM> dmError;
        List<Location> locationError;
        List<Project> projectError;
        List<WorkOrder> workOrderError;

        userError = userRepository.findUsersWithMissingFields();
        rateError = rateRepository.findRatesWithMissingFields();
        List<Holiday> holidays = holidayRepository.findAll();
        if (holidays.isEmpty()) {
            holidayError = true;
        } else {
            holidayError = false;
        }
        contractError = contractRepository.findContractsWithMissingFields();
        departmentError = departmentRepository.findDepartmentsWithMissingFields();
        dmError = dmRepository.findDMsWithMissingFields();
        locationError = locationRepository.findLocationsWithMissingFields();
        projectError = projectRepository.findProjectsWithMissingFields();
        workOrderError = workOrderRepository.findWorkOrdersWithMissingFields();

        adminErrorDTO.setUserError(userError);
        adminErrorDTO.setRateError(rateError);
        adminErrorDTO.setHolidayError(holidayError);
        adminErrorDTO.setContractError(contractError);
        adminErrorDTO.setDepartmentError(departmentError);
        adminErrorDTO.setDmError(dmError);
        adminErrorDTO.setLocationError(locationError);
        adminErrorDTO.setProjectError(projectError);
        adminErrorDTO.setWorkOrderError(workOrderError);

        return adminErrorDTO;

    }
}
