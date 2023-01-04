package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.task.Task;

import java.util.Comparator;

public class TaskIdComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {

        if (task1.getTaskId() > task2.getTaskId()) {
            return 1;

        } else if (task1.getTaskId() < task2.getTaskId()) {
            return -1;

        } else {
            return 0;
        }
    }
}