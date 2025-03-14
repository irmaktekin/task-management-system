package com.irmaktekin.task.management.system.service.impl;

import com.irmaktekin.task.management.system.common.exception.ProjectNotFoundException;
import com.irmaktekin.task.management.system.dto.request.ProjectCreateRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Override
    public Project createProject(ProjectCreateRequest projectCreateRequest) {
        return null;
    }

    @Override
    public Project updateProject(Project project) throws ProjectNotFoundException {
        return null;
    }

    @Override
    public Page<ProjectDto> getProjects(int page, int size) {
        return null;
    }

    @Override
    public Project findProjectById(UUID id) throws ProjectNotFoundException {
        return null;
    }
}
