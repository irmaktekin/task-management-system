package com.irmaktekin.task.management.system.entity;

import com.irmaktekin.task.management.system.common.ProjectStatus;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private String departmentName;
    private String description;
    private String title;
    private List<User> members;

}
