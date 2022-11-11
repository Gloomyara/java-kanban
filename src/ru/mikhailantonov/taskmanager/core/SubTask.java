package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class SubTask extends Task {
    public SubTask(int taskId, String taskName, String taskDescription) {
        taskCreateDate = Calendar.getInstance();
        this.taskName = "Подзадача " + taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = StatusType.NEW;
        this.taskId = taskId;
    }
}
