package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.InvalidTaskStateException;
import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.dto.response.TaskStatusResponse;
import com.irmaktekin.task.management.system.entity.Comment;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.enums.TaskPriority;
import com.irmaktekin.task.management.system.enums.TaskState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {
    Page<TaskDto> getTasks(Pageable pageable);

    TaskDto createTask(TaskCreateRequest taskCreateRequest);

    TaskDto assignTaskToUser(UUID taskId, UUID userId);

    TaskDto getTaskProgress(UUID taskId);

    TaskDto updateTaskState(UUID taskId, TaskStatusUpdateRequest request) throws InvalidTaskStateException, TaskNotFoundException;
}

    /*
    TaskDto updateTask(UUID taskId,TaskUpdateRequest taskUpdateRequest) throws TaskNotFoundException;
    Task changePriority(UUID taskId, TaskPriority taskPriority);
    void softDeleteTask(UUID taskId);
    Comment addCommentToTask(UUID taskId,String content);
    TaskDto updateTaskDetails(UUID taskId, TaskDetailsUpdateRequest request);

}*/
