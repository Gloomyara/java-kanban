package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.history.HistoryManager;
import ru.mikhailantonov.taskmanager.manager.history.InMemoryHistoryManager;
import ru.mikhailantonov.taskmanager.manager.tasks.FileBackedTasksManager;
import ru.mikhailantonov.taskmanager.manager.tasks.HttpTaskManager;
import ru.mikhailantonov.taskmanager.manager.tasks.InMemoryTaskManager;
import ru.mikhailantonov.taskmanager.manager.tasks.TaskManager;

import java.io.IOException;

/**
 * Утилитарный класс для создания менеджеров
 * уже 3
 */

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HttpTaskManager getDefault(String serverUrl) {
        return new HttpTaskManager(serverUrl);
    }

    public static HttpTaskManager getDefault(String serverUrl, String apiToken) {
        return new HttpTaskManager(serverUrl, apiToken);
    }

    public static TaskManager getDefault(boolean isNew, String path) {
        try {
            if (!isNew) {
                return FileBackedTasksManager.loadFromFile(FileManager.createFile(path).toFile());
            } else {
                return new FileBackedTasksManager(FileManager.createFile(path).toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(boolean isNew, boolean isTest, String path) {
        try {
            if (isTest) {
                if (!isNew) {
                    return FileBackedTasksManager.loadFromFile(FileManager.createTestFile(path).toFile());
                } else {
                    return new FileBackedTasksManager(FileManager.createTestFile(path).toFile());
                }
            } else {
                if (!isNew) {
                    return FileBackedTasksManager.loadFromFile(FileManager.createFile(path).toFile());
                } else {
                    return new FileBackedTasksManager(FileManager.createFile(path).toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new InMemoryTaskManager();
    }


    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
