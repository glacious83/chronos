package com.chronos.timereg.controller;

import com.chronos.timereg.dto.AdminErrorDTO;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.User;
import com.chronos.timereg.model.enums.RoleName;
import com.chronos.timereg.repository.UserRepository;
import com.chronos.timereg.service.*;
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
    private final UserRepository userRepository;
    private final UserService userService;

    public AdminController(ComprehensiveImportService comprehensiveImportService, UserImportService userImportService, RateImportService rateImportService, AdminService adminService, UserRepository userRepository, UserService userService) {
        this.comprehensiveImportService = comprehensiveImportService;
        this.userImportService = userImportService;
        this.rateImportService = rateImportService;
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping(path = "/importTimeRegData", consumes = "multipart/form-data")
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

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<User> setUserActive(@PathVariable Long id, @RequestParam boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        user.setActive(active);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<User> setUserApproved(@PathVariable Long id, @RequestParam boolean approved) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        user.setApproved(approved);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users/{id}/roles")
    public ResponseEntity<Void> addRole(
            @PathVariable Long id,
            @RequestParam RoleName[] roles
    ) {
        userService.addRoleToUser(id, roles);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}/roles")
    public ResponseEntity<Void> removeRole(
            @PathVariable Long id,
            @RequestParam RoleName role
    ) {
        userService.removeRoleFromUser(id, role);
        return ResponseEntity.noContent().build();
    }
}
