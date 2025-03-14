package com.irmaktekin.task.management.system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean isActive;

    @OneToMany
    private List<Task> tasks;

    @Column(nullable = false,updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false,updatable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}