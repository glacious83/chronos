package com.chronos.timereg.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "dms")
public class DM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique DM code (e.g., "DM-1244")
    @Column(unique = true, nullable = false)
    private String code;

    // Optional description
    private String description;

    // Budget for the DM; must be non-negative.
    @Column(nullable = false)
    private BigDecimal budget;

    // Start date of the DM validity (duration)
    @Column(nullable = false)
    private LocalDate startDate;

    // End date of the DM validity (duration)
    @Column(nullable = false)
    private LocalDate endDate;
}
