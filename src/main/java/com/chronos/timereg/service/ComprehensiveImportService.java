package com.chronos.timereg.service;

import org.springframework.web.multipart.MultipartFile;

public interface ComprehensiveImportService {
    void importTimeRegData(MultipartFile file);
}
