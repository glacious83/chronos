package com.chronos.timereg.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserImportService {
    void importUsers(MultipartFile file);
}
