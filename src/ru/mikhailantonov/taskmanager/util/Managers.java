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
            //t = new InMemoryTaskManager();
            try {
                t = FileBackedTasksManager.loadFromFile(FileManager.createFile("autosave.csv").toFile());
            } catch (IOException e){
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
