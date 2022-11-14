package ru.mikhailantonov.taskmanager.core;

public class SubTask extends Task {
    private int epicTaskId;

    public SubTask(int epicTaskId, String taskName, String taskDescription, StatusType taskStatus) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
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
