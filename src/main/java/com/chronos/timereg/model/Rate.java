package com.chronos.timereg.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rates")
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user title for which this rate applies, e.g.,
    // "Automation Test Designer Junior" or "QA Senior"
    @Column(nullable = false, unique = true)
    private String userTitle;

    // The rate in Euros for that title. Using BigDecimal for currency precision.
    @Column(nullable = false)
    private BigDecimal rate;
}
