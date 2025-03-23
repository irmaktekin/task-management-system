package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.InvalidTaskStateException;
import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface TaskService {
    Page<TaskDto> getTasks(Pageable pageable);
    TaskDto createTask(TaskCreateRequest taskCreateRequest) throws Exception;
    TaskDto assignTaskToUser(UUID taskId, UUID userId);
    TaskDto getTaskProgress(UUID taskId);
    TaskDto assignPriority(UUID taskId, TaskPriority taskPriority);
    TaskDto addAttachmentToTask(UUID taskId, MultipartFile file) throws Exception;
    TaskDto updateTaskState(UUID taskId, TaskStatusUpdateRequest request) throws InvalidTaskStateException, TaskNotFoundException;
    TaskDto updateTaskDetails(UUID taskId, TaskDetailsUpdateRequest request);
    Boolean softDeleteTask(UUID projectId);
}
