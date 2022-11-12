package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Задача. В ней могут содержаться сообщения, есть статус, даты создания, обновления и закрытия.
 * Обновление - любое изменение с помощью сообщений.
 */

public class TaskObject {



    private boolean isEpic;
    private boolean isSub;
    private int taskId;
    private String taskName;
    private StatusType taskStatus;
    private String taskDescription;
    private Calendar taskCreateDate;
    private Calendar taskUpdateDate;
    private Calendar taskCloseDate = null;
    private People taskAuthor;
    private People taskAppoint;

    public boolean isEpic() {
        return isEpic;
    }

    public void setEpic(boolean epic) {
        isEpic = epic;
    }

    public boolean isSub() {
        return isSub;
    }

    public void setSub(boolean sub) {
        isSub = sub;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskCloseDate(Calendar taskCloseDate) {
        this.taskCloseDate = taskCloseDate;
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
}
