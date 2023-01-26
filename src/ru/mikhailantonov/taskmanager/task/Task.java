package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.FileManager;
import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/**
 * Родительский класс для объектов всех задач
 */

public class Task {
    protected Integer taskId;
    protected String taskName;
    protected StatusType taskStatus;
    protected String taskDescription;
    protected LocalDateTime startTime = null;

    protected LocalDateTime updateTime = null;
    protected Duration duration = Duration.ofMinutes(0);
    protected LocalDateTime endTime = null;
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

    public Task(String taskName, StatusType taskStatus, String taskDescription, Integer durationInMinutes) {

        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.duration = Duration.ofMinutes(durationInMinutes);
    }

    public Task(LocalDateTime startTime, String taskName, StatusType taskStatus,
                String taskDescription, Integer durationInMinutes) {
        this.startTime = startTime;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.duration = Duration.ofMinutes(durationInMinutes);
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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskId() {
        return this.taskId;
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

    public LocalDateTime getEndTime() {
        return endTime = startTime.plus(duration);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (taskId != null) {
            hash = hash + taskId.hashCode();
        }
        hash = hash * 31;
        if (taskName != null) {
            hash = taskName.hashCode();
        }
        hash = hash * 31;
        if (taskDescription != null) {
            hash = hash + taskDescription.hashCode();
        }
        hash = hash * 31;
        if (duration != null) {
            hash = hash + duration.hashCode();
        }
        hash = hash * 31;
        if (startTime != null) {
            hash = hash + startTime.hashCode();
        }
        hash = hash * 31;
        if (taskStatus != null) {
            hash = hash + taskStatus.hashCode();
        }
        hash = hash * 31;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Task otherTask = (Task) obj;

        return Objects.equals(taskId, otherTask.taskId) &&
                Objects.equals(taskName, otherTask.taskName) &&
                Objects.equals(taskDescription, otherTask.taskDescription) &&
                Objects.equals(duration, otherTask.duration) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(taskStatus, otherTask.taskStatus);
    }

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        String result;
        if (startTime == null) {
            result = "null";
        } else {
            result = startTime.format(FileManager.DATE_TIME_FORMATTER);
        }
        return result + "," + taskId + "," + taskType.getTaskTypeName() + "," + taskName
                + "," + taskStatus.getStatusName() + "," + taskDescription + "," + duration.toMinutes();
    }
}
