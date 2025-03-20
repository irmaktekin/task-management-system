package com.irmaktekin.task.management.system.common.mapper;

import com.irmaktekin.task.management.system.config.DefaultMapStructConfiguration;
import com.irmaktekin.task.management.system.dto.request.TaskCreateRequest;
import com.irmaktekin.task.management.system.dto.request.TaskStatusUpdateRequest;
import com.irmaktekin.task.management.system.dto.response.TaskDto;
import com.irmaktekin.task.management.system.entity.Task;
import com.irmaktekin.task.management.system.enums.TaskState;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = DefaultMapStructConfiguration.class)
public interface TaskMapper {

    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "state",source="state",qualifiedByName= "mapState")
    TaskDto convertToDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task taskCreateRequestToTask(TaskCreateRequest taskCreateRequest);

    @Named("mapState")
    default TaskState mapTaskState(String taskState){
        return TaskState.valueOf(taskState);
    }

}
