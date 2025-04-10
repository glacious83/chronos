package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.*;
import com.chronos.timereg.model.enums.*;
import com.chronos.timereg.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ComprehensiveImportServiceImpl implements ComprehensiveImportService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ProjectRepository projectRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final DMRepository dmRepository;
    private final LeaveEntryRepository leaveEntryRepository;
    private final HolidayRepository holidayRepository;
    private final ContractRepository contractRepository;

    public ComprehensiveImportServiceImpl(UserRepository userRepository, CompanyRepository companyRepository, DepartmentRepository departmentRepository, WorkOrderRepository workOrderRepository, ProjectRepository projectRepository, TimeEntryRepository timeEntryRepository, DMRepository dmRepository, LeaveEntryRepository leaveEntryRepository, HolidayRepository holidayRepository, ContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
        this.workOrderRepository = workOrderRepository;
        this.projectRepository = projectRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.dmRepository = dmRepository;
        this.leaveEntryRepository = leaveEntryRepository;
        this.holidayRepository = holidayRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public void importTimeRegData(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            log.info("Importing time registration data from file: {}", file.getOriginalFilename());

            // Assuming the sheet is named "Users"
            Sheet sheet = workbook.getSheet("timereg");
            if (sheet == null) {
                throw new BusinessException("Excel file must contain a sheet named 'Users'.");
            }

            int rowCount = sheet.getLastRowNum();
            // Process rows starting at row 1 (assuming row 0 is header)
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                log.info("Processing row: {}", i+1);

                // Mapping columns based on the headers:
                // 0: Date
                LocalDate date = parseDate(row.getCell(0));

                // 1: Vendor Name -> Company name
                String companyName = getCellString(row.getCell(1));

                // 2: Resource Surname -> User.lastName
                String surname = getCellString(row.getCell(2)).trim();

                // 3: Resource Name -> User.firstName
                String firstName = getCellString(row.getCell(3)).trim();

                // 4: SAP # -> (User.sapId). For lookup.
                String sapId = getCellString(row.getCell(4));

                // 5: Profile -> User.title
                String profile = getCellString(row.getCell(5));

                // 6: Contract -> WorkOrder title
                String woTitle = getCellString(row.getCell(6));

                // 7: WO -> WorkOrder code
                String woCode = getCellString(row.getCell(7));

                // 8: Request Status -> WorkOrder status (string, e.g., APPROVED, PENDING_APPROVAL, etc.)
                String requestStatusStr = getCellString(row.getCell(8));

                // 9: NBG IT Unit -> Department name
                String deptName = getCellString(row.getCell(9));

                // 10: NBG Requestor Name -> Manager full name
                String managerLastName = getCellString(row.getCell(10));

                // 11: Project # -> DM code (for DM entity)
                String dmCode = getCellString(row.getCell(11));

                // 12: Project Name -> Project name
                String projectName = getCellString(row.getCell(12));

                // 13: Actuals MHs -> TimeEntry worked hours
                String actualsMHsStr = getCellString(row.getCell(13));
                double actualsMHs = parseDouble(actualsMHsStr);

                // Process Company: Lookup or create.
                Company company = companyRepository.findByName(companyName)
                        .orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setName(companyName);
                            // Companies imported from this sheet are external unless the company name equals "NBG".
                            newCompany.setExternal(!"NBG".equalsIgnoreCase(companyName));
                            return companyRepository.save(newCompany);
                        });

                // Process Department: Lookup or create.
                Department department = departmentRepository.findByCode(deptName)
                        .orElseGet(() -> {
                            Department dept = new Department();
                            dept.setCode(deptName);
                            return departmentRepository.save(dept);
                        });

                // Process Manager: Lookup managerLastName.
                User manager = null;
                if (!managerLastName.isEmpty()) {
                    Optional<User> mgrOpt = userRepository.findByLastName(managerLastName);
                    if (mgrOpt.isPresent()) {
                        manager = mgrOpt.get();
                    } else {
                        throw new BusinessException("Manager not found: " + managerLastName);
                    }
                }

                // Process User: Lookup by SAP id if provided; otherwise, by first and last name.
                Optional<User> userOpt;
                userOpt = userRepository.findByFirstNameAndLastName(firstName, surname);
                User user;
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                } else {
                   log.warn("User not found: {} {}", firstName, surname);
                    user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(surname);
                    user.setTitle(profile);
                    user.setEmail(surname.toLowerCase() + "." + firstName.toLowerCase() + "@nbg.gr");
                    user.setEmployeeId("E" + Math.abs((int) (Math.random() * 1000000))); // Random employee ID for new users.
                }
                if (!sapId.isEmpty() && !sapId.equals("0")) {
                    user.setSapId(sapId);
                }
                // Now associate User with Company and, if applicable, Location.
                if (user.getCompany() == null) {
                    user.setCompany(company);
                }

                // (Note: If you want to update the user's department and responsible manager if missing:)
                if (user.getDepartment() == null) {
                    user.setDepartment(department);
                }
                if (user.getResponsibleManager() == null && manager != null) {
                    user.setResponsibleManager(manager);
                }
                // Save the user.
                user = userRepository.save(user);

                if ("OOO".equalsIgnoreCase(dmCode)) {
                    // In this case, ignore the WO and Project fields.
                    // Leave type is determined by Actuals MHs: 8 = FULL, 4 = HALF.
                    if (actualsMHs == 8) {
                        // Create a full day leave.
                        LeaveEntry leave = new LeaveEntry();
                        leave.setUser(user);
                        leave.setDate(date);
                        leave.setLeaveType(LeaveType.FULL);
                        leave.setLeaveStatus(com.chronos.timereg.model.enums.LeaveStatus.APPROVED);
                        leaveEntryRepository.save(leave);
                    } else if (actualsMHs == 4) {
                        // Create a half day leave.
                        LeaveEntry leave = new LeaveEntry();
                        leave.setUser(user);
                        leave.setDate(date);
                        leave.setLeaveType(LeaveType.FIRST_HALF);
                        leave.setLeaveStatus(LeaveStatus.APPROVED);
                        leaveEntryRepository.save(leave);
                    } else if (actualsMHs > 0) {
                        LeaveEntry leave = new LeaveEntry();
                        leave.setUser(user);
                        leave.setDate(date);
                        leave.setLeaveType(LeaveType.PARTIAL_LEAVE);
                        leave.setLeaveStatus(LeaveStatus.APPROVED);
                        leaveEntryRepository.save(leave);
                    } else {
                        log.warn("Invalid leave hours for OOO: {}. User: {}", actualsMHs, user.getLastName() + ' ' + user.getFirstName());
                        continue;
                    }
                    // Skip processing DM, WO, and Project for this row.
                    continue;
                }

                // Process Work Order: Lookup or create by WO code.
                if (!woCode.isEmpty()) {
                    Optional<WorkOrder> woOpt = workOrderRepository.findByCode(woCode);
                    WorkOrder workOrder;
                    if (woOpt.isPresent()) {
                        workOrder = woOpt.get();
                    } else {
                        workOrder = new WorkOrder();
                        workOrder.setCode(woCode);
                        workOrder.setCreatedDate(LocalDateTime.now());
                        workOrder.setCompany(company);
                    }
                    workOrder.setTitle(woTitle); // WO title from "Contract" column.
                    try {
                        WOStatus woStatus = WOStatus.valueOf(requestStatusStr.trim().toUpperCase());
                        workOrder.setStatus(woStatus);
                    } catch (IllegalArgumentException e) {
                        workOrder.setStatus(WOStatus.PENDING_APPROVAL);
                    }
                    workOrder.setPm(manager != null ? manager.getFirstName() + " " + manager.getLastName() : "Unknown");
                    // (Budget, dates for WO can be left null or given default values)
                    workOrderRepository.save(workOrder);
                }

                // Process DM: Lookup or create by DM code (from column 11).
                DM dm = null;
                if (!dmCode.isEmpty()) {
                    Optional<DM> dmOpt = dmRepository.findByCode(dmCode);
                    if (dmOpt.isPresent()) {
                        dm = dmOpt.get();
                    } else {
                        dm = new DM();
                        dm.setCode(dmCode);
                        // For defaults, set budget = 0 and duration from today to one month later.
                        dm.setBudget(BigDecimal.ZERO);
                        dm = dmRepository.save(dm);
                    }
                }

                // Process Project: Use Project Name from column 12.
                // We assume that if a DM is provided, we link the project to that DM.
                if (!projectName.isEmpty()) {
                    // In our Project entity, we now add a field "code" to match DM code, if desired.
                    Optional<Project> projOpt = projectRepository.findByName(dmCode);
                    Project project;
                    if (projOpt.isPresent()) {
                        project = projOpt.get();
                    } else {
                        project = new Project();
                        project.setName(dmCode);  // Use DM code as the unique identifier if available.
                    }
                    project.setName(projectName);
                    project.setDepartmentResponsible(department);
                    project.setDm(dm);
                    projectRepository.save(project);
                }

                User finalUser = user;
                contractRepository.findByUser_Id(user.getId())
                        .orElseGet(() -> {
                            Contract newContract = new Contract();
                            newContract.setUser(finalUser);
                            return contractRepository.save(newContract);
                        });

                // Process Time Entry: Create a time registration record using the Date (col0) and Actuals MHs (col13).
                TimeEntry timeEntry = new TimeEntry();
                timeEntry.setUser(user);
                timeEntry.setDate(date);
                timeEntry.setWorkedHours(actualsMHs);
                timeEntry.setOvertimeHours(0.0);
                // Compute specialDayType here as needed; for simplicity, set to NORMAL.

                TimeEntryServiceImpl timeEntryService = new TimeEntryServiceImpl(timeEntryRepository, userRepository, leaveEntryRepository, holidayRepository, contractRepository);
                timeEntry.setSpecialDayType(timeEntryService.determineSpecialDayType(date));
                timeEntry.setWorkLocation(WorkLocation.OFFICE);
                // Set default approval status.
                timeEntry.setApprovalStatus(ApprovalStatus.APPROVED);
                timeEntry.setApprovalTimestamp(LocalDateTime.of(date, LocalDateTime.now().toLocalTime()));
                if (dmCode.equals("OOO")) {
                    timeEntry.setIsLeave(true);
                }
                timeEntry.setApprovedBy(manager);
                timeEntryRepository.save(timeEntry);
            }
            log.info("Time registration data import completed successfully.");
        } catch (Exception e) {
            throw new BusinessException("Error importing users: " + e.getMessage());
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue()).trim();
        }
        return "";
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) {
            throw new BusinessException("Date cell is missing.");
        }
        if(cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else {
                // If the numeric cell isn't formatted as a date,
                // you might assume it's an Excel date value.
                return LocalDate.of(1899, 12, 30).plusDays((long) cell.getNumericCellValue());
            }
        } else if(cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            try {
                return LocalDate.parse(dateStr);
            } catch(Exception e) {
                throw new BusinessException("Invalid date format: " + dateStr);
            }
        }
        throw new BusinessException("Unable to parse date from cell.");
    }

    private double parseDouble(String numStr) {
        try {
            return Double.parseDouble(numStr);
        } catch (NumberFormatException e) {
            throw new BusinessException("Invalid numeric format: " + numStr);
        }
    }
}
