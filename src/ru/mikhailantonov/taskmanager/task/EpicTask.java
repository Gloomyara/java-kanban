package ru.mikhailantonov.taskmanager.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EpicTask extends Task {

    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    public EpicTask(String taskName, String taskDescription) {

        this.taskName = taskName;
        this.taskDescription = taskDescription;
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
                if (object.getTaskStatus().equals(StatusType.NEW)) {
                    check1.add(i);
                } else if (object.getTaskStatus().equals(StatusType.DONE)) {
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

    @Override
    public String toString() {
        String result = "{ID: " + taskId + " Эпик: " + taskName + " " + taskStatus;
        if (subTaskMap != null) {
            result = result + "\n Подзадачи: " + subTaskMap.values();
        }
        return result + "}\n";
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }
}