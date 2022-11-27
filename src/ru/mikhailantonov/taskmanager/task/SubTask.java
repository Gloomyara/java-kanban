package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.StatusType;

/** Класс для объектов подзадач */

public class SubTask extends Task {
    private int epicTaskId;

    public SubTask(int epicTaskId, String taskName, String taskDescription, StatusType taskStatus) {
        super(taskName, taskDescription, taskStatus);
        this.epicTaskId = epicTaskId;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "{ID: " + taskId + " Подзадача: " + taskName + " " + taskStatus + "}\n";
    }
}
