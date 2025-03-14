package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;

import java.util.UUID;

public record TaskUpdateRequest(String description,
                                TaskPriority taskPriority,
                                TaskState taskState,
                                UUID assigneeId,
                                String acceptanceCriteria) {
}
