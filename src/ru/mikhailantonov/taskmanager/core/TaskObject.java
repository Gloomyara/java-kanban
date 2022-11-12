package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;

public class TaskObject {

    private Integer taskId;
    private Integer epicTaskId;
    private String taskName;
    private StatusType taskStatus;
    private String taskDescription;
    private Calendar taskCreateDate;
    private Calendar taskUpdateDate;
    private Calendar taskCloseDate = null;


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

    public String toString() {
        return "Номер задачи: " + taskId + " Название задачи: " + taskName + " Create date:" +
                taskCreateDate.getTime() + " Update date:" + taskUpdateDate.getTime();
    }
}
