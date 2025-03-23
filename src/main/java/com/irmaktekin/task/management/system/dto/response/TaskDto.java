package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;

import java.util.List;
import java.util.UUID;

public record TaskDto(UUID id,
                      String description,
                      TaskPriority priority,
                      TaskState state,
                      UserDto assignee,
                      String acceptanceCriteria,
                      List<CommentDto> comments,
                      boolean deleted,
                      String title,
                      String reason,
                      UUID projectId) {
}
