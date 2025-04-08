package com.chronos.timereg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String firstName;

    private String lastName;
    private String middleName;

    @Email
    @NotBlank
    private String email;

    // Employee ID will also be used as the username
    @NotBlank
    private String employeeId;

    @NotBlank
    private String password; // In production, store hashed passwords

    private String title;

    @NotNull
    private Long departmentId;  // The ID of the department the user belongs to

    // Optional: The ID of the responsible manager (if any)
    private Long responsibleManagerId;
}
