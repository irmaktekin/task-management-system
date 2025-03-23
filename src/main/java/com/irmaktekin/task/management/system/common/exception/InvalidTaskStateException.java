package com.irmaktekin.task.management.system.common.exception;

public class InvalidTaskStateException extends RuntimeException{
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
