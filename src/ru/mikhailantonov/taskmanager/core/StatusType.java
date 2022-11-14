package ru.mikhailantonov.taskmanager.core;

public enum StatusType {
    //Перечисляем варианты состояний задачи и их названия
    NEW("Открыт"),
    IN_PROGRESS("В работе"),
    DONE("Закрыт");

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
