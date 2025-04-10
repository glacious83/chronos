package com.chronos.timereg.controller;

import com.chronos.timereg.service.ComprehensiveImportService;
import com.chronos.timereg.service.RateImportService;
import com.chronos.timereg.service.UserImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/timereg/import")
public class TimeRegImportController {
    private final ComprehensiveImportService comprehensiveImportService;
    private final UserImportService userImportService;
    private final RateImportService rateImportService;

    public TimeRegImportController(ComprehensiveImportService comprehensiveImportService, UserImportService userImportService, RateImportService rateImportService) {
        this.comprehensiveImportService = comprehensiveImportService;
        this.userImportService = userImportService;
        this.rateImportService = rateImportService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> importTimeRegData(@RequestParam("file") MultipartFile file) {
        rateImportService.importRates(file);
        userImportService.importUsers(file);
        comprehensiveImportService.importTimeRegData(file);
        return ResponseEntity.ok("Time registration data imported successfully.");
    }
}
