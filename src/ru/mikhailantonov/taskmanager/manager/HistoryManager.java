package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

import java.util.ArrayList;

/**
 * Базовый интерфейс менеджеров по хранению истории
 */

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}

