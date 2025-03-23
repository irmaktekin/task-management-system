package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.validation.constraints.NotNull;

public record TaskStatusUpdateRequest(
        @NotNull  TaskState state,
        String reason) {}
