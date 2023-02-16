package ru.mikhailantonov.taskmanager.server.enums;

public enum KVKeys {
    //Перечисляем варианты состояний задачи и их названия
    TASKS("tasks"),
    EPICS("epics"),
    SUBTASKS("subtasks"),
    HISTORY("history");

    private final String keyName;

    KVKeys(String keyName) {
        this.keyName = keyName;
    }

    public String getName() {
        return keyName;
    }

    public static KVKeys fromString(String str) {
        for (KVKeys keyName : KVKeys.values()) {
            if (keyName.getName().equalsIgnoreCase(str)) return keyName;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Ключ: " + keyName;
    }
}

