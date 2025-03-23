package com.irmaktekin.task.management.system.common.exception;

public class TaskStateAlreadySameException extends RuntimeException {

    public TaskStateAlreadySameException(String message){
        super(message);
    }
}
