package com.irmaktekin.task.management.system.entity;

import com.irmaktekin.task.management.system.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false)
    private String departmentName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String title;

    private boolean isDeleted;

    @OneToMany
    private List<User> members;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL)
    private List<Task> tasks;
}