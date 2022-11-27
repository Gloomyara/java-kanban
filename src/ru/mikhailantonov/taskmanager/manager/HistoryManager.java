package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

/** Базовый интерфейс менеджеров по хранению истории */

public interface HistoryManager {
    void add(Task task);
    String getHistory();
}

