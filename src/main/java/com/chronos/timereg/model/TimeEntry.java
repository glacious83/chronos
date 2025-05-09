package com.chronos.timereg.model;

import com.chronos.timereg.model.enums.ApprovalStatus;
import com.chronos.timereg.model.enums.SpecialDayType;
import com.chronos.timereg.model.enums.WorkLocation;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "time_entries")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")  // no unique constraint
    private Project project;

    @NotNull
    private LocalDate date;

    private double workedHours;
    private double overtimeHours;

    @Enumerated(EnumType.STRING)
    @NotNull
    private WorkLocation workLocation;

    // System-managed field representing the computed special day type.
    @Enumerated(EnumType.STRING)
    private SpecialDayType specialDayType = SpecialDayType.NORMAL;

    private double compensationHours;

    @NotNull
    private Boolean isLeave = false; // Set default value as false

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private LocalDateTime approvalTimestamp;
}
