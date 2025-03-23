package com.irmaktekin.task.management.system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String description;

    @Column
    private String acceptanceCriteria;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskState state;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskPriority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "task",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Attachment> attachments;


    @OneToMany(mappedBy = "task",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments;

    private String reason;

    private boolean deleted;

    private String title;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;
}
