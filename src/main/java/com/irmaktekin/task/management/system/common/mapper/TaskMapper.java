package com.irmaktekin.task.management.system.common.mapper;

import com.irmaktekin.task.management.system.config.DefaultMapStructConfiguration;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Project;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.enums.TaskState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(config = DefaultMapStructConfiguration.class)
public interface TaskMapper {

    @Mapping(target = "state",source="state",qualifiedByName= "mapState")
    @Mapping(target = "projectId", source = "project.id")
    TaskDto convertToDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "projectId")
    Task taskCreateRequestToTask(TaskCreateRequest taskCreateRequest);

    @Named("mapState")
    default TaskState mapTaskState(String taskState){
        return TaskState.valueOf(taskState);
    }

    default Project map(UUID projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
}
