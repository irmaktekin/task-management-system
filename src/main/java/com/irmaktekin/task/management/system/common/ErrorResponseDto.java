package com.irmaktekin.task.management.system.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ErrorResponseDto {
    private String message;
    private int statusCode;
    private String errorType;
    private Map<String, String> validationErrors;

}
