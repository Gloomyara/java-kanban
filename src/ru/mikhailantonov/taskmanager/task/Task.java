package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.task.enums.TaskType;
import ru.mikhailantonov.taskmanager.util.FileManager;

import java.time.Duration;
import java.time.LocalDateTime;
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

    protected Duration duration = Duration.ofMinutes(0);
    protected LocalDateTime endTime = null;
    protected TaskType taskType;

    public Task(String taskName, String taskDescription) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;
        taskType = TaskType.TASK;
    }

    public Task(Integer taskId, String taskName, String taskDescription) {

        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        taskType = TaskType.TASK;
    }

    public Task(String taskName, StatusType taskStatus, String taskDescription) {

        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        taskType = TaskType.TASK;
    }

    public Task(String taskName, StatusType taskStatus, String taskDescription, Integer durationInMinutes) {

        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.duration = duration.plusMinutes(durationInMinutes);
        taskType = TaskType.TASK;
    }

    public Task(LocalDateTime startTime, String taskName, StatusType taskStatus,
                String taskDescription) {
        this.startTime = startTime;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        taskType = TaskType.TASK;
    }

    public Task(LocalDateTime startTime, String taskName, StatusType taskStatus,
                String taskDescription, Integer durationInMinutes) {
        this.startTime = startTime;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.duration = duration.plusMinutes(durationInMinutes);
        taskType = TaskType.TASK;
    }

    public Task(Integer taskId, String taskName, StatusType taskStatus, String taskDescription,
                LocalDateTime startTime, Integer durationInMinutes) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = duration.plusMinutes(durationInMinutes);
        taskType = TaskType.TASK;
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

    public Duration getDuration() {
        return duration;
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
