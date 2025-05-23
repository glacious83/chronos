package com.chronos.timereg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    private String description;

    // Department responsible for the project
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department departmentResponsible;

    // Department responsible for the project
    @ManyToOne
    @JoinColumn(name = "dm_id")
    private DM dm;

    // Project Manager
    @ManyToOne
    @JoinColumn(name = "pm_id")
    private User pm;
}
