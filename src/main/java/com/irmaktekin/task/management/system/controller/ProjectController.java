package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "api/v1/projects",produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /*@PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "List all projects with pagination")
    @GetMapping
    public Page<ProjectDto> getProjects(@PageableDefault(page = 0,size = 20) Pageable pageable){
        return projectService.getProjects(pageable);
    }*/

    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Operation(summary = "Create project")
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectRequest projectRequest){
        ProjectDto createdProject = projectService.createProject(projectRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/projects/{id}")
                .buildAndExpand(createdProject.id())
                .toUri();

        return ResponseEntity.created(location).body(createdProject);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update project by ID")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID projectId, @Valid @RequestBody ProjectRequest projectRequest){
        return ResponseEntity.ok(projectService.updateProject(projectId,projectRequest));
    }
/*
    @PutMapping("/{projectId}")
    public ResponseEntity<Void> softDeleteProject(@PathVariable UUID projectId){
        projectService.softDeleteProject(projectId);
        return ResponseEntity.noContent().build();
    }*/

    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('TEAM_LEADER')")
    @Operation(summary = "Add task to the project")
    @PostMapping("/{projectId}")
    public ResponseEntity<TaskDto> addTaskToProject(@PathVariable UUID projectId, @Valid @RequestBody TaskCreateRequest taskCreateRequest){
        TaskDto taskDto = projectService.addTaskToProject(projectId,taskCreateRequest);
        return ResponseEntity.ok().body(taskDto);
    }
}
