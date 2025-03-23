package com.irmaktekin.task.management.system.common.exception;

public class TaskAlreadyCompletedException extends RuntimeException{

    public TaskAlreadyCompletedException(String message) {
        super(message);
    }
}
