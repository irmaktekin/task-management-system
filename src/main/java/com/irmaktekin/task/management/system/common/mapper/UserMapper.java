package com.irmaktekin.task.management.system.common.mapper;

import com.irmaktekin.task.management.system.config.DefaultMapStructConfiguration;
import com.irmaktekin.task.management.system.dto.response.UserDto;
import com.irmaktekin.task.management.system.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = DefaultMapStructConfiguration.class)
public interface UserMapper {

    UserDto convertToDto(User user);

    default List<UserDto> converToDtoList(List<User> users){
        return users.stream().map(this::convertToDto).toList();
    }
}
