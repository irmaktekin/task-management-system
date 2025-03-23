package com.irmaktekin.task.management.system.common.mapper;

import com.irmaktekin.task.management.system.config.DefaultMapStructConfiguration;
import com.irmaktekin.task.management.system.dto.request.ProjectRequest;
import com.irmaktekin.task.management.system.dto.response.ProjectDto;
import com.irmaktekin.task.management.system.entity.Project;
import org.mapstruct.*;

@Mapper(config = DefaultMapStructConfiguration.class,uses = UserMapper.class)
public interface ProjectMapper {

    @Mapping(source = "departmentName", target = "departmentName")
    @Mapping(source = "projectStatus", target = "status")
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Project projectRequestToProject(ProjectRequest projectRequest);

    @Mapping(source = "members", target = "memberIds")
    @Mapping(source = "status", target = "projectStatus")
    ProjectDto convertToDto(Project project);

}
