package com.chronos.timereg.model;

import com.chronos.timereg.model.enums.EmploymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false)
    private LocalTime workingHoursStart;

    @Column(nullable = false)
    private LocalTime workingHoursEnd;

    // Applicable only for INTERNAL
    private Integer maxAnnualLeave;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    private LocalDate contractEndDate;

    // Defaults: days per week at office/home (can be changed)
    private Integer daysOfficePerWeek = 3;
    private Integer daysHomePerWeek = 2;

    // Optional: trial period duration in months (for external only)
    private Integer trialPeriodMonths;
}
