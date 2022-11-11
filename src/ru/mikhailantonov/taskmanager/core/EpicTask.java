package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class EpicTask extends Task {

    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    protected boolean isEpic = true;

    public EpicTask(int taskId, String taskName, String taskDescription) {
        taskCreateDate = Calendar.getInstance();
        this.taskName = "Эпик " + taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = StatusType.NEW;
        this.taskId = taskId;
    }

    public void addSubTask(int taskId, String taskName, String taskDescription) {

        if (!subTasks.containsKey(taskId)) {
            subTasks.put(taskId, new SubTask(taskId, taskName, taskDescription));
        }

    }

    public StatusType epicStatusType() {
        int counterNew = 0;
        int counterDone = 0;
        if (subTasks == null) return StatusType.NEW;
        for (SubTask oneStringTask : subTasks.values()) {
            if ((oneStringTask.taskStatus == null) || (oneStringTask.taskStatus.equals(StatusType.NEW))) {
                counterNew++;
            } else if (oneStringTask.taskStatus.equals(StatusType.DONE)) {
                counterDone++;
            }
        }

        if (counterNew == subTasks.size()) {
            return StatusType.NEW;
        } else if (counterDone == subTasks.size()) {
            return StatusType.DONE;
        }
        return StatusType.IN_PROGRESS;
    }

    public String deleteOneTask(int taskId) {
        if (subTasks.containsKey(taskId)) {
            subTasks.remove(taskId);
            return "Задача удалена.";
        }
        return "Задачи под таким ID нет.";
    }
    @Override
    public void printAllTasks() {
        if (subTasks != null) {
            for (Task oneTask : subTasks.values()) {
                System.out.println(oneTask.taskName);
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }
}