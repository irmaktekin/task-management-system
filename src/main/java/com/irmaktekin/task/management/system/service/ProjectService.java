package com.irmaktekin.task.management.system.service;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.dto.response.TaskDto;

import java.util.UUID;

public interface ProjectService {
    ProjectDto createProject(ProjectRequest projectRequest);
    TaskDto addTaskToProject(UUID projectId, TaskCreateRequest taskCreateRequest);
    ProjectDto updateProject(UUID projectId,ProjectRequest projectRequest) throws ProjectNotFoundException;
    Boolean softDeleteProject(UUID projectId);
}
