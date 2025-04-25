package com.chronos.timereg.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String middleName;

    @Column(unique = true, nullable = false)
    private String email;

    // Employee id (could be used as username)
    @Column(unique = true, nullable = false)
    private String employeeId;

    @NotBlank
    private String password; // In production, store hashed passwords.

    // Additional new fields:
    @Column(unique = true)
    private String sapId;         // SAP identifier

    private String vm;            // Virtual machine identifier

    private String ip;            // IP address

    private String phone;         // Phone number

    // Many-to-one relationship to Location entity
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // Other fields like title, department, responsibleManager, etc.
    private String title;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonManagedReference
    private Department department;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @JsonBackReference
    private User responsibleManager;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private boolean active = true;
    private boolean approved = false;
}
