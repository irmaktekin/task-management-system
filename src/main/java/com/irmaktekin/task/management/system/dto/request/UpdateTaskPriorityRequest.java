package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskPriorityRequest(@NotNull TaskPriority taskPriority) {}
