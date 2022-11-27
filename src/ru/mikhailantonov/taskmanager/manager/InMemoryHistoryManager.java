package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

import java.util.ArrayList;

/** Класс для хранения истории просмотров задач, через методы getTaskByID */

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<String> taskRequestHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (taskRequestHistory.size() < 10) {
            taskRequestHistory.add(task.getTaskName());
        } else {
            taskRequestHistory.remove(0);
            taskRequestHistory.add(task.getTaskName());
        }
    }

    @Override
    public String getHistory() {
        return taskRequestHistory.toString();
    }
}
