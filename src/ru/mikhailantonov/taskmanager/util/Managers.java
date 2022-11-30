package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.*;
import ru.mikhailantonov.taskmanager.task.Task;

import java.util.ArrayList;

/**
 * Утилитарный класс для создания менеджеров
 * когда-нибудь он сможет создавать больше 1 типа менеджера
 */

public class Managers {
    private static TaskManager t;
    private static HistoryManager h;

    public static TaskManager getDefault() {
        if (t == null) {
            t = new InMemoryTaskManager();
        }
        return t;
    }

    public static HistoryManager getHistoryManager() {
        if (h == null) {
            h = new InMemoryHistoryManager();
        }
        return h;
    }

    public static ArrayList<Task> getDefaultHistory(TaskManager taskManager) {
        return taskManager.getHistory();
    }
}
