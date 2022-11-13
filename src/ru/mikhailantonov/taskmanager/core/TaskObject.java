package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class TaskObject {

    HashMap<Integer, TaskObject> subTaskMap;
    private final boolean isEpic;
    private Integer taskId;
    private Integer epicTaskId;
    private String taskName;
    private StatusType taskStatus;
    private String taskDescription;
    private Calendar taskCreateDate;
    private Calendar taskUpdateDate;
    private Calendar taskCloseDate = null;

    TaskObject(Integer epicTaskId, boolean isEpic, String taskName, String taskDescription, StatusType taskStatus) {
        this.isEpic = isEpic;
        this.epicTaskId = epicTaskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public boolean isEpic() {
        return isEpic;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setCloseDate(Calendar date) {
        taskCloseDate = date;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskId() {
        return this.taskId;
    }

    public Calendar getTaskUpdateDate() {
        return this.taskUpdateDate;
    }

    void setTaskUpdateDate(Calendar date) {
        taskUpdateDate = date;
    }

    public Calendar getTaskCloseDate() {
        if (taskCloseDate != null) return this.taskCloseDate;
        return null;
    }

    public Calendar getTaskCreateDate() {
        return this.taskCreateDate;
    }

    public void setTaskCreateDate(Calendar date) {
        taskCreateDate = date;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public StatusType getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(StatusType statusType) {
        this.taskStatus = statusType;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        String result = "{ Номер задачи: " + taskId + " Эпик?: " + isEpic
                + " Название задачи: " + taskName + " " + taskStatus + "}";
        if (subTaskMap != null) {
            result = result + "Подзадачи: " + subTaskMap;
        }
        return result;
    }
}
