package ru.mikhailantonov.taskmanager.util;

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
    public static StatusType stringToStatus(String str){
        switch (str) {
            case "NEW":
                return NEW;
            case "IN_PROGRESS":
                return IN_PROGRESS;
            case "DONE":
                return DONE;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Статус задачи: " + statusName;
    }
}
