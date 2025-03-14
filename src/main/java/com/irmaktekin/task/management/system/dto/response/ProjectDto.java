package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record ProjectDto(
        @NotNull UUID id,

        @NotNull ProjectStatus projectStatus,

        @NotBlank
        @Size(min=10, max=200) String responsibleDepartmentName,

        @NotBlank
        @Size(min=5, max=500) String description,

        @NotBlank
        @Size(max=200) String title,

        @NotNull
        @Size(min = 1) List<UUID> memberIds
) {};
