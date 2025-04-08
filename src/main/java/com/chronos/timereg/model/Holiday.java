package com.chronos.timereg.model;

import com.chronos.timereg.model.enums.SpecialDayType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "holidays")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the holiday, e.g. "Labor Day", "Good Thursday", etc.
    @Column(nullable = false)
    private String name;

    // Date of the holiday.
    @Column(nullable = false)
    private LocalDate date;

    // If true, the holiday is a half-day (e.g. Good Thursday)
    private boolean halfDay;

    // Optionally, an admin can specify an override special day type.
    @Enumerated(EnumType.STRING)
    private SpecialDayType specialDayType;
}
