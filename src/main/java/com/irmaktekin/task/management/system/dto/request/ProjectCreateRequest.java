package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.ProjectStatus;

import java.util.List;

public record ProjectCreateRequest(ProjectStatus projectStatus,
                                   String responsibleDepartmentName,
                                   String description,
                                   String title,
                                   List<String> memberIds) {}
