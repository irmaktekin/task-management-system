package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.TaskNotFoundException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.repository.TaskRepository;
import com.irmaktekin.task.management.system.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(@Valid TaskCreateRequest taskCreateRequest) {
        Task createdTask = Task.builder().userStoryDescription(taskCreateRequest.description())
                .taskPriority(taskCreateRequest.taskPriority())
                .taskState(taskCreateRequest.taskState())
                .acceptanceCriteria(taskCreateRequest.acceptanceCriteria())
                .userStoryDescription(taskCreateRequest.description())
                .build();
        return taskRepository.save(createdTask);
    }

    @Override
    public Task updateTask(Task task) throws TaskNotFoundException {
        Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(()->new TaskNotFoundException("Task not found with id: "+task.getId()));
        existingTask.setTaskPriority(task.getTaskPriority());
        existingTask.setAssignee(task.getAssignee());
        existingTask.setTaskPriority(task.getTaskPriority());
        existingTask.setAcceptanceCriteria(task.getAcceptanceCriteria());
        return taskRepository.save(existingTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getTasks(int page, int size) {
        return taskRepository.getTasks(PageRequest.of(page,size));
    }

    @Override
    public Task findTaskById(UUID id) throws TaskNotFoundException {
        return taskRepository.findById(id)
                .orElseThrow(()->new TaskNotFoundException("Task not found with id: "+ id));
    }
}
