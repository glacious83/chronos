package com.chronos.timereg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank
    private String code;  // Department code or name

    @NotNull
    private Long managerId;  // The ID of the user to be set as the manager
}
