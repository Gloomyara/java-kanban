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

    @Override
    public String toString() {
        return "Статус задачи: " + statusName;
    }
}
