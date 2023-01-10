package ru.mikhailantonov.taskmanager.util;

import java.util.HashMap;

/**
 * Enum со статусами задач
 */

public enum StatusType {
    //Перечисляем варианты состояний задачи и их названия
    NEW("NEW"),
    IN_PROGRESS("IN_PROGRESS"),
    DONE("DONE");

    private final String statusName;

    StatusType(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public static StatusType fromString(String str){
        for (StatusType type: StatusType.values()){
            if (type.getStatusName().equalsIgnoreCase(str)) return type;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Статус задачи: " + statusName;
    }
}
