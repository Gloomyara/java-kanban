package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

import java.util.List;

/**
 * Базовый интерфейс менеджеров по хранению истории
 */

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

}

