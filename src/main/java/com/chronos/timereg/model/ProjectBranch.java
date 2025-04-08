package com.chronos.timereg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "project_branches")
public class ProjectBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    // Many branches belong to one Project
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // Assigned users: Many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "projectbranch_users",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> assignedUsers;
}
