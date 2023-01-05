package ru.mikhailantonov.taskmanager.task;

import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TaskType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Класс для объектов Эпик задач
 */

public class EpicTask extends Task {

    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    private final TaskType taskType = TaskType.EPIC;

    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription);
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
                setCloseDate(Calendar.getInstance());
            } else {
                status = StatusType.IN_PROGRESS;
            }
        }
        return status;
    }

    //id,type,name,status,description,epic
    @Override
    public String toString() {
        return taskId + "," + taskType.getTaskTypeName() + "," + taskName + "," + taskStatus.getStatusName() + "," + taskDescription;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }
}