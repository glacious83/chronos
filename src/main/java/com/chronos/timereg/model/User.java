package com.chronos.timereg.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    private String lastName;

    private String middleName;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    // Employee ID is also used as the username
    @NotBlank
    @Column(unique = true, nullable = false)
    private String employeeId;

    @NotBlank
    private String password; // In production, store hashed passwords.

    private String title;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Self-reference to designate the responsible manager
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User responsibleManager;
}
