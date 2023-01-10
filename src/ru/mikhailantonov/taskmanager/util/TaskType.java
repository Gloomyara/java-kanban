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

    public static TaskType fromString(String str){
        for (TaskType type: TaskType.values()){
            if (type.getTaskTypeName().equalsIgnoreCase(str)) return type;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Тип задачи: " + taskTypeName;
    }
}
