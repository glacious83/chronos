package com.chronos.timereg.controller;

import com.chronos.timereg.dto.AdminErrorDTO;
import com.chronos.timereg.dto.LeavesReportDTO;
import com.chronos.timereg.service.AdminService;
import com.chronos.timereg.service.ComprehensiveImportService;
import com.chronos.timereg.service.RateImportService;
import com.chronos.timereg.service.UserImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final ComprehensiveImportService comprehensiveImportService;
    private final UserImportService userImportService;
    private final RateImportService rateImportService;
    private final AdminService adminService;

    public AdminController(ComprehensiveImportService comprehensiveImportService, UserImportService userImportService, RateImportService rateImportService, AdminService adminService) {
        this.comprehensiveImportService = comprehensiveImportService;
        this.userImportService = userImportService;
        this.rateImportService = rateImportService;
        this.adminService = adminService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> importTimeRegData(@RequestParam("file") MultipartFile file) {
        rateImportService.importRates(file);
        userImportService.importUsers(file);
        comprehensiveImportService.importTimeRegData(file);
        return ResponseEntity.ok("Time registration data imported successfully.");
    }

    @GetMapping("/checkErrors")
    public ResponseEntity<AdminErrorDTO> checkErrors() {
        AdminErrorDTO adminErrorDTO = adminService.checkErrors();
        return ResponseEntity.ok(adminErrorDTO);
    }
}
