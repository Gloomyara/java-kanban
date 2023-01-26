package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Утилитарный класс для создания менеджеров
 * уже 3
 */

public class Managers {
    private static TaskManager t;
    private static HistoryManager h;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");


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
