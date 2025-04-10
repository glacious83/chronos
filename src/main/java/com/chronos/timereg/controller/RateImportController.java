package com.chronos.timereg.controller;

import com.chronos.timereg.service.RateImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rates/import")
public class RateImportController {

    private final RateImportService rateImportService;

    public RateImportController(RateImportService rateImportService) {
        this.rateImportService = rateImportService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importRates(@RequestParam("file") MultipartFile file) {
        rateImportService.importRates(file);
        return ResponseEntity.ok("Rates imported successfully.");
    }

}
