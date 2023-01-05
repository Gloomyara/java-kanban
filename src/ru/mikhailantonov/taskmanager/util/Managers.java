package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.*;

/**
 * Утилитарный класс для создания менеджеров
 * когда-нибудь он сможет создавать больше 1 типа менеджера
 */

public class Managers {
    private static TaskManager t;
    private static HistoryManager h;

    public static TaskManager getDefault() {
        if (t == null) {
            t = new FileBackedTasksManager();
            //t = new InMemoryTaskManager();
        }
        return t;
    }

    public static HistoryManager getDefaultHistory() {
        if (h == null) {
            h = new InMemoryHistoryManager();
        }
        return h;
    }
}
