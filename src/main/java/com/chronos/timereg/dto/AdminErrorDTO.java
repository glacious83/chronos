package com.chronos.timereg.dto;

import com.chronos.timereg.model.*;
import lombok.Data;

import java.util.List;

@Data
public class AdminErrorDTO {
    private List<Company> companyError;
    private List<Rate> rateError;
    private boolean holidayError;
    private List<User> userError;
    private List<Contract> contractError;
    private List<Department> departmentError;
    private List<DM> dmError;
    private List<Location> locationError;
    private List<Project> projectError;
    private List<WorkOrder> workOrderError;
}
