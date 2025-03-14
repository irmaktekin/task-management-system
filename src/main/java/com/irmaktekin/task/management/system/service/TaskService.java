package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TaskService {
    Task createTask(TaskCreateRequest taskCreateRequest);
    Task updateTask(Task task) throws TaskNotFoundException;
    Page<TaskDto> getTasks(int page, int size);
    Task findTaskById(UUID id) throws TaskNotFoundException;
}
