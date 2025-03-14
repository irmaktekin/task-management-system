package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;

import java.util.UUID;

public record TaskDto(UUID id,
                      String description,
                      TaskPriority taskPriority,
                      TaskState taskState,
                      UUID assigneeId,
                      String assigneeName,
                      String acceptanceCriteria) {
}
