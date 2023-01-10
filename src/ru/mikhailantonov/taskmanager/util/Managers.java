package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.*;

import java.io.IOException;

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

    public static TaskManager getDefault(boolean isNew, String path) {
        if (t == null) {
            try {
                if (!isNew) {
                    t = FileBackedTasksManager.loadFromFile(FileManager.createFile(path).toFile());
                } else {
                    t = new FileBackedTasksManager(FileManager.createFile(path).toFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
