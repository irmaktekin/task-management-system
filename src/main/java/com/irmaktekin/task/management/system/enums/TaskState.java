package com.irmaktekin.task.management.system.enums;

public enum TaskState {
    BACKLOG,
    IN_ANALYSIS,
    IN_DEVELOPMENT,
    COMPLETED;

    @Override
    public String toString(){
        return name();
    }
}
