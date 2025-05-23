package com.chronos.timereg.model;

import com.chronos.timereg.model.enums.WOStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique work order code (e.g., "WO-1244")
    @Column(unique = true, nullable = false)
    private String code;

    // Link to the company; now using the new Company entity
    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    // Project Manager (PM) of the work order (assumed as a string for now)
    private String pm;

    // Budget allocated for the work order
    private BigDecimal budget;

    // Status of the work order.
    @Enumerated(EnumType.STRING)
    private WOStatus status;

    // Title or name of the work order.
    private String title;

    // Optional description.
    private String description;

    // Additional date fields:
    // When the work order is scheduled to start.
    private LocalDate startDate;

    // When the work order is scheduled to end.
    private LocalDate endDate;

    // Optional deadline or due date.
    private LocalDate deadline;

    // Automatically set creation timestamp.
    private LocalDateTime createdDate = LocalDateTime.now();

    // Updated timestamp.
    private LocalDateTime updatedDate;
}
