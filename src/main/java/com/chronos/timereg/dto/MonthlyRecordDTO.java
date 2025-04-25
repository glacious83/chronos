package com.chronos.timereg.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MonthlyRecordDTO {
    private LocalDate date;
    private String vendorName;
    private String resourceSurname;
    private String resourceName;
    private String sapNumber;
    private String profile;
    private String nbgItUnit;
    private String nbgRequestorName;
    private String projectNumber;
    private String projectName;
    private double actuals;
    private boolean leave;           // true if this row comes from a leave
}