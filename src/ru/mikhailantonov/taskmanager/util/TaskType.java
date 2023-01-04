package ru.mikhailantonov.taskmanager.util;

/**
 * Enum с типом задач
 */

public enum TaskType {
    //Перечисляем типы задач;
    TASK("Задача"),
    EPIC("Эпик"),
    SUBTASK("Подзадача");

    private final String statusName;

    TaskType(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    @Override
    public String toString() {
        return statusName;
    }
}
