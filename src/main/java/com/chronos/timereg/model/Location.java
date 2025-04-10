package com.chronos.timereg.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // City name, e.g., "Athens"
    @Column(nullable = false)
    private String cityName;

    // Country name, e.g., "Greece"
    @Column(nullable = false)
    private String country;

    // Country code, e.g., "GR"
    @Column(nullable = false)
    private String countryCode;
}
