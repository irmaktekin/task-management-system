package com.irmaktekin.task.management.system.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TaskDetailsUpdateRequest(
        @NotBlank String description,
        @NotBlank String title)
{}
