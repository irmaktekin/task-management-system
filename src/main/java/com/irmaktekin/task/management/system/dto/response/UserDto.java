package com.irmaktekin.task.management.system.dto.response;

import java.util.UUID;

public record UserDto (UUID id,
                       String fullName,
                       String email,
                       boolean isActive){
}
