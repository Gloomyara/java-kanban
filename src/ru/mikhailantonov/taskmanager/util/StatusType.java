package ru.mikhailantonov.taskmanager.util;

/**
 * Enum со статусами задач
 */

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
    public static StatusType stringToStatus(String str){
        switch (str) {
            case "Открыт":
                return NEW;
            case "В работе":
                return IN_PROGRESS;
            case "Закрыт":
                return DONE;
        }
        return null;
    }
    @Override
    public String toString() {
        return "Статус задачи: " + statusName;
    }
}
