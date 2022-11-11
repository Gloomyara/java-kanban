package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Задача. В ней могут содержаться сообщения, есть статус, даты создания, обновления и закрытия.
 * Обновление - любое изменение с помощью сообщений.
 */

public class Task {



    protected boolean isEpic = false;
    protected int taskId;
    protected String taskName;
    protected StatusType taskStatus;
    protected String taskDescription;
    protected Calendar taskCreateDate;
    protected Calendar taskUpdateDate;
    protected Calendar taskCloseDate = null;
    protected People taskAuthor;
    protected People taskAppoint;

    public Task() {

    }

    public Task(int taskId, String taskName, String taskDescription) {
        taskCreateDate = Calendar.getInstance();
        this.taskName = "Задача " + taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = StatusType.NEW;
        this.taskId = taskId;
    }


    void setCloseDate(Calendar date) {
        taskCloseDate = date;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public int getTaskId() {
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

    public People getTaskAuthor() {
        return this.taskAuthor;
    }

    public void setTaskAuthor(People taskAuthor) {
        this.taskAuthor = taskAuthor;
    }

    public People getTaskAppoint() {
        return this.taskAppoint;
    }

    public void setTaskAppoint(People taskAppoint) {
        this.taskAppoint = taskAppoint;
    }

    public StatusType getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(StatusType statusType) {
        this.taskStatus = statusType;
    }

    public String toString() {
        return "Номер задачи: " + taskId + " Название задачи: " + taskName + " Create date:" +
                taskCreateDate.getTime() + " Update date:" + taskUpdateDate.getTime();
    }

    public void printAllTasks() {
    }
}
