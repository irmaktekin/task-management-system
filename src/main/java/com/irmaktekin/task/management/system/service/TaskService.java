package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {
    Task createTask(TaskCreateRequest taskCreateRequest);
    Task updateTask(Task task) throws TaskNotFoundException;
    Page<TaskDto> getTasks(Pageable pageable);
    Task findTaskById(UUID id) throws TaskNotFoundException;
}
