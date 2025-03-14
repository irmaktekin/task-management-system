package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public Task createTask(TaskCreateRequest taskCreateRequest) {
        return null;
    }

    @Override
    public Task updateTask(Task task) throws TaskNotFoundException {
        return null;
    }

    @Override
    public Page<TaskDto> getTasks(int page, int size) {
        return null;
    }

    @Override
    public Task findTaskById(UUID id) throws TaskNotFoundException {
        return null;
    }
}
