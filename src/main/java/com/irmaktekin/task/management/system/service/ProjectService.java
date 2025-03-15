package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {
    Project createProject(ProjectCreateRequest projectCreateRequest);
    Project updateProject(Project project) throws ProjectNotFoundException;
    Page<ProjectDto> getProjects(Pageable pageable);
    Project findProjectById(UUID id) throws ProjectNotFoundException;
}
