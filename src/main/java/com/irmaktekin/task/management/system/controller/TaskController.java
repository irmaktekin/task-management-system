package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.common.exception.TaskReasonRequiredException;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.enums.TaskState;
import com.irmaktekin.task.management.system.service.AttachmentService;
import com.irmaktekin.task.management.system.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.Socket;
import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping(value = "api/v1/tasks",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;
    private final AttachmentService attachmentService;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);


    public TaskController(TaskService taskService, AttachmentService attachmentService) {
        this.taskService = taskService;
        this.attachmentService = attachmentService;
    }

    @GetMapping
    @Operation(summary = "List all tasks with pagination")
    public Page<TaskDto> getTasks(@PageableDefault(page = 0,size = 20) Pageable pageable){
        return taskService.getTasks(pageable);
    }

    /*@GetMapping("/{id}")
    @Operation(summary = "Get task by id")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable UUID id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }*/

    @PostMapping
    @Operation(summary = "Save task")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskCreateRequest taskCreateRequest){
        TaskDto createdTask = taskService.createTask(taskCreateRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/tasks/{id}")
                .buildAndExpand(createdTask.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTask);
    }

/*
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskUpdateRequest));
    }*/

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @PutMapping("/{taskId}/assignee/{userId}")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskDto> assignTaskToUser(@PathVariable UUID taskId, @PathVariable UUID userId){
        return ResponseEntity.ok(taskService.assignTaskToUser(taskId,userId));
    }
/*
    @PutMapping("/{id}/priority")
    public ResponseEntity<Task> changePriority(@PathVariable UUID id, @RequestBody UpdateTaskPriorityRequest taskPriorityRequest){
        Task updatedTask = taskService.changePriority(id,taskPriorityRequest.taskPriority());
        return ResponseEntity.ok(updatedTask);
    }
*/
    @Operation(summary = "Display the status of the task")
    @GetMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> getTaskProgress(@PathVariable UUID taskId){
        TaskDto taskDto = taskService.getTaskProgress(taskId);
        return ResponseEntity.ok(taskDto);
    }
/*
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> softDeleteTask(@PathVariable UUID taskId){
        taskService.softDeleteTask(taskId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{taskId}/details")
    public ResponseEntity<TaskDto> updateTaskDetails(@PathVariable UUID taskId, @Valid @RequestBody TaskDetailsUpdateRequest request){
        TaskDto taskDto = taskService.updateTaskDetails(taskId,request);
        return ResponseEntity.ok(taskDto);
    }

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<String> addAttachmentToTask(@PathVariable UUID taskId,@RequestParam MultipartFile file) throws Exception {
        attachmentService.addAttachmentToTask(taskId,file);
        return ResponseEntity.ok("Success");
    }
*/
    @Operation(summary = "Update the state of the task")
    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDto> updateTaskState(@PathVariable UUID taskId, @RequestBody TaskStatusUpdateRequest request) throws Exception {
        logger.debug("Updating task state with state: {}, reason: {}", request.state(), request.reason());

        TaskDto  updatedTask = taskService.updateTaskState(taskId,request);
        return ResponseEntity.ok(updatedTask);
    }

}
