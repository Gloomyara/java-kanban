package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.task.enums.TaskType;
import ru.mikhailantonov.taskmanager.util.FileManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Класс для объектов Эпик задач
 */

public class EpicTask extends Task {

    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription);
        this.taskType = TaskType.EPIC;
    }

    public EpicTask(Integer taskId, String taskName, String taskDescription) {
        super(taskId, taskName, taskDescription);
        this.taskType = TaskType.EPIC;
    }

    public StatusType epicStatusType() {
        ArrayList<Integer> check1 = new ArrayList<>();
        ArrayList<Integer> check2 = new ArrayList<>();
        StatusType status;

        if (subTaskMap.isEmpty()) {
            return StatusType.NEW;
        } else {

            for (int i : subTaskMap.keySet()) {
                SubTask object = subTaskMap.get(i);
                if (StatusType.NEW.equals(object.getTaskStatus())) {
                    check1.add(i);
                } else if (StatusType.DONE.equals(object.getTaskStatus())) {
                    check2.add(i);
                }
            }
            if (check1.size() == subTaskMap.size()) {
                status = StatusType.NEW;
            } else if (check2.size() == subTaskMap.size()) {
                status = StatusType.DONE;
            } else {
                status = StatusType.IN_PROGRESS;
            }
        }
        return status;
    }

    public void setEpicDuration() {
        this.duration = Duration.ofMinutes(0);
        if (subTaskMap.isEmpty()) {
            this.duration = Duration.ofMinutes(0);
        } else {
            for (SubTask object : subTaskMap.values()) {
                if (object.getDuration() != null) {
                    this.duration = duration.plus(object.getDuration());
                }
            }
        }
    }

    public void setEpicStartTime() {
        if (subTaskMap.isEmpty()) {
            startTime = null;
        } else {
            for (SubTask object : subTaskMap.values()) {
                if (startTime == null || startTime.isAfter(object.getStartTime())) {
                    startTime = object.startTime;
                }
            }
        }
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
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
        hash = hash + subTaskMap.hashCode();
        hash = hash * 31;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        EpicTask otherTask = (EpicTask) obj;

        return Objects.equals(taskId, otherTask.taskId) &&
                Objects.equals(taskName, otherTask.taskName) &&
                Objects.equals(taskDescription, otherTask.taskDescription) &&
                Objects.equals(duration, otherTask.duration) &&
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(taskStatus, otherTask.taskStatus) &&
                Objects.equals(subTaskMap, otherTask.subTaskMap);
    }

    @Override
    public String toString() {
        String result;
        if (startTime == null) {
            result = "null";
        } else {
            result = startTime.format(FileManager.DATE_TIME_FORMATTER);
        }
        return result + "," + taskId + "," + taskType.getTaskTypeName() + "," + taskName + ","
                + taskStatus.getStatusName() + "," + taskDescription + "," + duration.toMinutes();
    }
}