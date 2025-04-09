package com.chronos.timereg.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one association with User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private LocalTime workingHoursStart;

    @Column(nullable = false)
    private LocalTime workingHoursEnd;

    // For internal employees only (if external, this remains null)
    private Integer maxAnnualLeave;

    @Column(nullable = false)
    private LocalDate contractStartDate;

    // Nullable for permanent contracts
    private LocalDate contractEndDate;

    // Default working days per week
    private Integer daysOfficePerWeek = 3;
    private Integer daysHomePerWeek = 2;

    // For external employees (trial period in months); if non-null, the user is external
    private Integer trialPeriodMonths;
}
