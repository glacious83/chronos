package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Company;
import com.chronos.timereg.model.Location;
import com.chronos.timereg.model.User;
import com.chronos.timereg.repository.CompanyRepository;
import com.chronos.timereg.repository.LocationRepository;
import com.chronos.timereg.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserImportServiceImpl implements UserImportService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserImportServiceImpl(UserRepository userRepository,
                                 CompanyRepository companyRepository,
                                 LocationRepository locationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void importUsers(MultipartFile file) {
        log.info("Starting user import from file: {}", file.getOriginalFilename());
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // Get the sheet named "Users"
            Sheet sheet = workbook.getSheet("Users");
            if (sheet == null) {
                return;
            }

            // Assume header is in row 0 with columns:
            // 0: Company
            // 1: Role
            // 2: Surname
            // 3: Name
            // 4: C&R (company name + position, only for externals; now ignored)
            // 5: email
            // 6: Exxxxx (employee code)
            // 7: VM
            // 8: IP
            // 9: tel
            // 10: location
            int rowCount = sheet.getLastRowNum();
            for (int i = 1; i <= rowCount; i++) {  // Starting from row 1 (row 0 is header)
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                // Read columns using fixed indices.
                String companyName = getCellString(row.getCell(0));
                String role = getCellString(row.getCell(1));
                String surname = getCellString(row.getCell(2)).trim();
                String firstName = getCellString(row.getCell(3)).trim();
                String crValue = getCellString(row.getCell(4));  // previously used for external info (ignored now)
                String email = getCellString(row.getCell(5));
                String employeeId = getCellString(row.getCell(6));
                String vm = getCellString(row.getCell(7));
                String ip = getCellString(row.getCell(8));
                String phone = getCellString(row.getCell(9));
                String locationStr = getCellString(row.getCell(10));

                // Mandatory validations: employeeId, email, surname, firstName, and role must be non-empty.
                if (employeeId.isEmpty() || email.isEmpty() || surname.isEmpty() ||
                        firstName.isEmpty() || role.isEmpty()) {
                    // Optionally log error for row number and skip row.
                    continue;
                }

                // Lookup or create Company based on the Company column.
                Company company = companyRepository.findByName(companyName)
                        .orElseGet(() -> {
                            Company newCompany = new Company();
                            newCompany.setName(companyName);
                            // Mark as external if C&R is non-empty.
                            newCompany.setExternal(!crValue.isEmpty());
                            return companyRepository.save(newCompany);
                        });

                // Lookup or create Location based on the location string.
                Location location = null;
                if (!locationStr.isEmpty()) {
                    Optional<Location> locOpt = locationRepository.findByCityName(locationStr);
                    location = locOpt.orElseGet(() -> {
                        Location loc = new Location();
                        loc.setCityName(locationStr);
                        loc.setCountry("Unknown");
                        loc.setCountryCode("");
                        return locationRepository.save(loc);
                    });
                }

                // Create or update the User.
                Optional<User> userOpt = userRepository.findByEmployeeId(employeeId);
                User user;
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                } else {
                    user = new User();
                    user.setEmployeeId(employeeId);
                }
                user.setFirstName(firstName);
                user.setLastName(surname);
                user.setEmail(email);
                user.setTitle(role);
                user.setVm(vm);
                user.setIp(ip);
                user.setPhone(phone);
                user.setPassword(passwordEncoder.encode("Test1234!"));  // Default password, should be changed by the user.
                user.setApproved(false);  // Default to approved.
                user.setActive(false);

                // Now, update the user's company reference.
                user.setCompany(company);

                // Associate location if available.
                user.setLocation(location);

                userRepository.save(user);
            }
        } catch (Exception e) {
            throw new BusinessException("Error importing users: " + e.getMessage());
        }

        log.info("User import completed successfully.");
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
}
