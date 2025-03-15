package com.irmaktekin.task.management.system.controller;

import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @GetMapping
    public Page<ProjectDto> getProjects(@RequestParam(defaultValue = "0")int page,
                                        @RequestParam(defaultValue = "20") int size){
        return projectService.getProjects(PageRequest.of(page,size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id){
        return ResponseEntity.ok(projectService.findProjectById(id));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectCreateRequest projectCreateRequest){
        Project createdProject = projectService.createProject(projectCreateRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/projects/{id}")
                .buildAndExpand(createdProject.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @RequestBody Project project){
        return ResponseEntity.ok(projectService.updateProject(project));
    }
}
