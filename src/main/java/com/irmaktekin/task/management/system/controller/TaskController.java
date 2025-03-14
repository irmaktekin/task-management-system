package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/tasks",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping
    public Page<TaskDto> getTasks(@RequestParam(defaultValue = "0")int page,
                                  @RequestParam(defaultValue = "20") int size){
        return taskService.getTasks(page,size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id){
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    @PostMapping
    public ResponseEntity<Task> createUser(@RequestBody TaskCreateRequest taskCreateRequest){
        Task createdTask = taskService.createTask(taskCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/tasks/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, @RequestBody Task task){
        return ResponseEntity.ok(taskService.updateTask(task));
    }

}
