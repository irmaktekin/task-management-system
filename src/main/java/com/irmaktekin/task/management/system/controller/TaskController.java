package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskDetailsUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.request.UpdateTaskPriorityRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping(value = "api/v1/tasks",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "List all tasks with pagination")
    public Page<TaskDto> getTasks(@PageableDefault(page = 0,size = 20) Pageable pageable){
        return taskService.getTasks(pageable);
    }

    @PostMapping
    @Operation(summary = "Save task")
    public ResponseEntity<TaskDto> createTask( @Valid @RequestBody TaskCreateRequest taskCreateRequest) throws Exception{

        TaskDto createdTask = taskService.createTask(taskCreateRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/tasks/{id}")
                .buildAndExpand(createdTask.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTask);
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @PutMapping("/{taskId}/assignee/{userId}")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskDto> assignTaskToUser(@PathVariable UUID taskId, @PathVariable UUID userId){
        return ResponseEntity.ok(taskService.assignTaskToUser(taskId,userId));
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @PutMapping("/{taskId}/priority")
    @Operation(summary = "Assign priority to a task")
    public ResponseEntity<TaskDto> assignPriority(@PathVariable UUID taskId, @Valid @RequestBody UpdateTaskPriorityRequest taskPriorityRequest){
        TaskDto  updatedTask = taskService.assignPriority(taskId,taskPriorityRequest.taskPriority());
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @Operation(summary = "Display the task progress")
    @GetMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> getTaskProgress(@PathVariable UUID taskId){
        TaskDto taskDto = taskService.getTaskProgress(taskId);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<TaskDto> addAttachmentToTask(@PathVariable UUID taskId,@RequestParam MultipartFile file) throws Exception {

        TaskDto taskDto = taskService.addAttachmentToTask(taskId,file);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(summary = "Update the state of the task")
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskState(@PathVariable UUID taskId, @Valid @RequestBody TaskStatusUpdateRequest request) throws Exception {
        TaskDto  updatedTask = taskService.updateTaskState(taskId,request);
        return ResponseEntity.ok(updatedTask);
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @Operation(summary = "Update the title and description of the task")
    @PatchMapping("/{taskId}/details")
    public ResponseEntity<TaskDto> updateTaskDetails(@PathVariable UUID taskId, @Valid @RequestBody TaskDetailsUpdateRequest request){
        TaskDto taskDto = taskService.updateTaskDetails(taskId,request);
        return ResponseEntity.ok(taskDto);
    }

    @Operation(summary = "Delete task by id")
    @PutMapping("/soft-delete/{taskId}")
    public ResponseEntity<Boolean> softDeleteTask(@PathVariable UUID taskId){
        Boolean isDeleted = taskService.softDeleteTask(taskId);
        return ResponseEntity.ok(isDeleted);
    }

}
