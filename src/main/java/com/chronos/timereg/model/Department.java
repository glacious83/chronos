package com.chronos.timereg.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String code;  // Unique code for the department

    // Manager of the department (FK to User)
    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    private User manager;
}
