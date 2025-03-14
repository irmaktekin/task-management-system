package com.irmaktekin.task.management.system.entity;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userStoryDescription;

    @Column(nullable = false)
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskState taskState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority taskPriority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
}
