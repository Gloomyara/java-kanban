package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class SubTask extends Task {
    int epicId;
    public SubTask(int epicId, int taskId, String taskName, String taskDescription, StatusType statusName) {
        taskCreateDate = Calendar.getInstance();
        this.taskName = "Подзадача " + taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = statusName;
        this.taskId = taskId;
        this.epicId = epicId;
    }
}
