package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskStatusResponse(@NotNull UUID taskId, @NotNull TaskState taskState) { }
