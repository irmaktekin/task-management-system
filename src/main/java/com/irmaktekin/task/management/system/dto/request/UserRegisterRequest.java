package com.irmaktekin.task.management.system.dto.request;

import com.irmaktekin.task.management.system.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record UserRegisterRequest(

        @NotBlank(message = "Full name is required.")
        String fullName,
        @NotBlank(message = "Username is required.")
        String username,
        @NotBlank(message = "Password is required." )
        @Size(min = 8, message = "Password must be at least 8 characters.")
        String password,
        boolean active,
        @NotNull(message = "At least one role must be assigned.")
        Set<RoleType> roles,
        boolean deleted)
{}
