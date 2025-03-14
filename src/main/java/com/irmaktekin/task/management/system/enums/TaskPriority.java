package com.irmaktekin.task.management.system.enums;

public enum TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    @Override
    public String toString(){
        return name();
    }
}
