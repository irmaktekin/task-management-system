package com.irmaktekin.task.management.system.common.exception;

public class UserAlreadyAssignedToProjectException extends RuntimeException{
    public UserAlreadyAssignedToProjectException(String message) {
        super(message);
    }
}
