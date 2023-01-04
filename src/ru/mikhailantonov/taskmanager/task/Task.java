package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TaskType;

import java.util.Calendar;

/**
 * Родительский класс для объектов всех задач
 */

public class Task {
    protected Integer taskId;
    protected String taskName;
    protected StatusType taskStatus;
    protected String taskDescription;
    protected Calendar taskCreateDate;
    protected Calendar taskUpdateDate;
    protected Calendar taskCloseDate = null;

    private final TaskType taskType = TaskType.TASK;

    public Task(String taskName, String taskDescription) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;

    }

    public Task(String taskName, StatusType taskStatus, String taskDescription) {

        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;

    }

    public TaskType getTaskType() {
        return taskType;
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

    public void setTaskUpdateDate(Calendar date) {
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

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        return taskId + "," + taskType + "," + taskName + "," + taskStatus.getStatusName() + "," + taskDescription;
    }
}
