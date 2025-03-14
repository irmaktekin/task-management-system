package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record TaskCreateRequest(@NotBlank(message = "Description is required")String description,
                                @NotBlank(message = "Task Priority is required")TaskPriority taskPriority,
                                @NotBlank(message = "Task State is required")TaskState taskState,
                                UUID assigneeId,
                                @NotBlank(message = "Acceptance criteria is required")String acceptanceCriteria) {
}
