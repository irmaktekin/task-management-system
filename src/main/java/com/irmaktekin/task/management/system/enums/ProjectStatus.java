package com.irmaktekin.task.management.system.enums;

public enum ProjectStatus {
    IN_PROGRESS,
    CANCELLED,
    COMPLETED;

    @Override
    public String toString(){
        return name();
    }
}
