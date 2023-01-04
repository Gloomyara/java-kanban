package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TaskType;

/**
 * Класс для объектов подзадач
 */

public class SubTask extends Task {
    private int epicTaskId;
    private final TaskType taskType = TaskType.SUBTASK;

    public SubTask(String taskName,StatusType taskStatus, String taskDescription, int epicTaskId) {
        super(taskName,taskStatus, taskDescription);
        this.epicTaskId = epicTaskId;
    }

    public Integer getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        return taskId + "," + taskType + "," + taskName + "," + taskStatus.getStatusName() + "," + taskDescription + "," + epicTaskId;
    }
}
