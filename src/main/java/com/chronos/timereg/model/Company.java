package com.chronos.timereg.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Company name (unique)
    @Column(nullable = false, unique = true)
    private String name;

    // Flag indicating whether this is an external company
    @Column(nullable = false)
    private boolean isExternal;
}
