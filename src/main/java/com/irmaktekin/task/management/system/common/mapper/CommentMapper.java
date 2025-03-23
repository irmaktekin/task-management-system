package com.irmaktekin.task.management.system.common.mapper;

import com.irmaktekin.task.management.system.config.DefaultMapStructConfiguration;
import com.irmaktekin.task.management.system.dto.response.CommentDto;
import com.irmaktekin.task.management.system.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = DefaultMapStructConfiguration.class)
public interface CommentMapper {
    @Mapping(target = "userId", source = "user.id")
    CommentDto convertToDto(Comment comment);
}