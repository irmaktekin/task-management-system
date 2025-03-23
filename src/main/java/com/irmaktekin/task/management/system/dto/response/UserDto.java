package com.irmaktekin.task.management.system.dto.response;

import com.irmaktekin.task.management.system.entity.Role;
import com.irmaktekin.task.management.system.enums.RoleType;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserDto (UUID id,
                       String fullName,
                       String username,
                       boolean isActive
                       ){ }
