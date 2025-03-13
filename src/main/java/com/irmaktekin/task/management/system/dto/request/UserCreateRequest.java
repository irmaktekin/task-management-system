package com.irmaktekin.task.management.system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(@NotBlank(message = "Full name is required") String fullName,
                                @Email(message = "Email should be valid") String email,
                                @NotBlank(message = "Password is required") String password,
                                boolean isActive) {
}
