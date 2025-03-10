package com.irmaktekin.task.management.system.entity;

import com.irmaktekin.task.management.system.common.TaskPriority;
import com.irmaktekin.task.management.system.common.TaskState;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TaskState taskState;

    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority;

    private String title;
    private String description;
    private User assignee;
    private List<Comment> comments;
}
