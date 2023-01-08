package ru.mikhailantonov.taskmanager.util;

/**
 * Enum с типом задач
 */

public enum TaskType {
    //Перечисляем типы задач;
    TASK("TASK"),
    EPIC("EPIC"),
    SUBTASK("SUBTASK");

    private final String taskTypeName;

    TaskType(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    @Override
    public String toString() {
        return "Тип задачи: " + taskTypeName;
    }
}
