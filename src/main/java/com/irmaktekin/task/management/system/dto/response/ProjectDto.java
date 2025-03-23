package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.enums.ProjectStatus;

import java.util.List;
import java.util.UUID;

public record ProjectDto(
        UUID id,
        ProjectStatus projectStatus,
        String departmentName,
        String description,
        String title,
       List<UserDto> memberIds
) {};
