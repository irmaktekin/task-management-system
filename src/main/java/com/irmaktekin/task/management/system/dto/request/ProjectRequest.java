package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ProjectRequest(@NotNull ProjectStatus projectStatus,
                             @NotBlank String departmentName,
                             @NotBlank String status,
                             @NotBlank String description,
                             @NotBlank String title,
                             List<UUID> memberIds) {}
