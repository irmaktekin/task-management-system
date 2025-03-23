package com.irmaktekin.task.management.system.common.exception;

public class UnauthorizedActionException extends RuntimeException{
    public UnauthorizedActionException(String message){
        super(message);
    }
}
