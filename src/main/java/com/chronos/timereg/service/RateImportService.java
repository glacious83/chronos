package com.chronos.timereg.service;

import org.springframework.web.multipart.MultipartFile;

public interface RateImportService {
    void importRates(MultipartFile file);
}
