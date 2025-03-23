package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.User;
import com.irmaktekin.task.management.system.enums.TaskState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TaskCreateRequest(@NotBlank String description,
                                TaskState state,
                                User assignee,
                                String acceptanceCriteria,
                                String reason,
                                List <Comment> comments,
                                String title,
                                UUID projectId)
{}
