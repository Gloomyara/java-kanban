package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class EpicTask extends Task {

    private HashMap<Integer, SubTask> subTaskList = new HashMap<>();

    protected boolean isEpic = true;

    public EpicTask(int taskId, String taskName, String taskDescription) {
        taskCreateDate = Calendar.getInstance();
        this.taskName = "Эпик " + taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
    }

    public void addSubTask(SubTask subTask) {

        if (!subTaskList.containsKey(taskId)) {
            subTaskList.put(taskId, subTask);
        }

    }

    public StatusType epicStatusType() {
        int counterNew = 0;
        int counterDone = 0;
        if (subTaskList == null) return StatusType.NEW;
        for (SubTask oneStringTask : subTaskList.values()) {
            if ((oneStringTask.taskStatus == null) || (oneStringTask.taskStatus.equals(StatusType.NEW))) {
                counterNew++;
            } else if (oneStringTask.taskStatus.equals(StatusType.DONE)) {
                counterDone++;
            }
        }

        if (counterNew == subTaskList.size()) {
            return StatusType.NEW;
        } else if (counterDone == subTaskList.size()) {
            return StatusType.DONE;
        }
        return StatusType.IN_PROGRESS;
    }

    public String deleteOneTask(int taskId) {
        if (subTaskList.containsKey(taskId)) {
            subTaskList.remove(taskId);
            return "Задача удалена.";
        }
        return "Задачи под таким ID нет.";
    }

    @Override
    public void printAllTasks() {
        if (subTaskList != null) {
            for (Task oneTask : subTaskList.values()) {
                System.out.println(oneTask.taskName + ": " + taskName);
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }

    public void deleteAllSubTasks() {

        if (!subTaskList.isEmpty()) {
            subTaskList.clear();
        } else {
            System.out.println("В эпике - " + taskName + " нет подзадач");
        }
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTaskList;
    }
}