package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.FileManager;
import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс для объектов подзадач
 */

public class SubTask extends Task {
    private Integer epicTaskId;
    private final TaskType taskType = TaskType.SUBTASK;

    public SubTask(String taskName, StatusType taskStatus, String taskDescription, Integer epicTaskId) {
        super(taskName, taskStatus, taskDescription);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String taskName, StatusType taskStatus,
                   String taskDescription, Integer durationInMinutes, Integer epicTaskId) {
        super(taskName, taskStatus, taskDescription, durationInMinutes);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(LocalDateTime startTime, String taskName, StatusType taskStatus,
                   String taskDescription, Integer durationInMinutes, Integer epicTaskId) {
        super(startTime, taskName, taskStatus, taskDescription, durationInMinutes);
        this.epicTaskId = epicTaskId;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(Integer epicTaskId) {
        this.epicTaskId = epicTaskId;
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
        if (epicTaskId != null) {
            hash = hash + epicTaskId.hashCode();
        }
        hash = hash * 31;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SubTask otherTask = (SubTask) obj;

        return Objects.equals(taskId, otherTask.taskId) &&
                Objects.equals(epicTaskId, otherTask.epicTaskId) &&
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
        return result + "," + taskId + "," + taskType.getTaskTypeName() + "," + taskName + ","
                + taskStatus.getStatusName() + "," + taskDescription + "," + duration.toMinutes() + "," + epicTaskId;
    }
}
