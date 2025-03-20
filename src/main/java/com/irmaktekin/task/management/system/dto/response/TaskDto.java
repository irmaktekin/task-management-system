package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.entity.Attachment;
import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record TaskDto(UUID id,
                      String description,
                      TaskPriority priority,
                      TaskState state,
                      UserDto assignee,
                      List <Attachment> attachments,
                      String acceptanceCriteria,
                      List<Comment> comments,
                      boolean deleted,
                      String title) {
}
