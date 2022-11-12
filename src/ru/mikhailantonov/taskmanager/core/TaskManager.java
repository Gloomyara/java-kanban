package ru.mikhailantonov.taskmanager.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class TaskManager {

    int id = 0; //было нужно для тестов

    Task task = new Task();
    EpicTask epicTask = new EpicTask();
    SubTask subTask = new SubTask();
    private ArrayList<Integer> subTaskId = new ArrayList<>();
    private HashMap<Integer, ArrayList<Integer>> epicSubTaskIdMap = new HashMap<>();


    public void manageTask(TaskObject object) {

        Integer taskId = object.getTaskId();
        Integer epicTaskId = object.getEpicTaskId();
        //условие для создания подзадачи
        if ((epicTaskId != null) && (taskId != null)) {

            if (!subTask.taskMap.containsKey(taskId)) {

                object.setTaskCreateDate(Calendar.getInstance());
                id = subTask.createNewTask(object);//возвращает ид+1

                subTaskId = epicSubTaskIdMap.get(epicTaskId);
                subTaskId.add(taskId);
                epicSubTaskIdMap.put(epicTaskId, subTaskId);

            } else {
                if (object.getTaskStatus().equals(StatusType.DONE)) {
                    object.setCloseDate(Calendar.getInstance());
                } else {
                    object.setTaskUpdateDate(Calendar.getInstance());
                }
                TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
                epicObject.setTaskStatus(epicStatusType(epicTaskId));
                subTask.updateTask(object);
            }
        //условие для создания эпика
        } else if (epicTaskId != null) {
            if (!epicTask.taskMap.containsKey(epicTaskId)) {
                object.setTaskStatus(epicStatusType(epicTaskId));
                object.setTaskCreateDate(Calendar.getInstance());

                id = epicTask.createNewTask(object);//возвращает ид+1
                epicSubTaskIdMap.put(epicTaskId, subTaskId);
            } else {
                object.setTaskStatus(epicStatusType(epicTaskId));
                if (object.getTaskStatus().equals(StatusType.DONE)) {
                    object.setCloseDate(Calendar.getInstance());
                } else  {
                    object.setTaskUpdateDate(Calendar.getInstance());
                }
                epicTask.updateTask(object);
            }
        //условие для создания просто задачи
        } else if(taskId != null) {

            if (!task.taskMap.containsKey(taskId)) {
                object.setTaskCreateDate(Calendar.getInstance());
                id = task.createNewTask(object);//возвращает ид+1
            } else {

                if (object.getTaskStatus().equals(StatusType.DONE)) {
                    object.setCloseDate(Calendar.getInstance());
                } else {
                    object.setTaskUpdateDate(Calendar.getInstance());
                }
                task.updateTask(object);
            }
        } else {
            System.out.println("Ошибка! задаче не присвоен ID");
        }
    }
    //метод для статуса эпика
    public StatusType epicStatusType(int epicTaskId) {
        int counterNew = 0;
        int counterDone = 0;
        if (epicTask.taskMap.containsKey(epicTaskId) && epicSubTaskIdMap.containsKey(epicTaskId)) {
            if (epicSubTaskIdMap.get(epicTaskId) == null) return StatusType.NEW;
            subTaskId = epicSubTaskIdMap.get(epicTaskId);
            for (int taskId : subTaskId) {
                TaskObject object = subTask.taskMap.get(taskId);
                if ((object.getTaskStatus() == null) || (object.getTaskStatus().equals(StatusType.NEW))) {
                    counterNew++;
                } else if (object.getTaskStatus().equals(StatusType.DONE)) {
                    counterDone++;
                }
            }
            if (counterNew == subTask.taskMap.size()) {
                return StatusType.NEW;
            } else if (counterDone == subTask.taskMap.size()) {
                return StatusType.DONE;
            }
            return StatusType.IN_PROGRESS;
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
            return null;
        }
    }

    //метод для печати всех подзадач 1 эпика
    void printOneEpicSubTasks (int epicTaskId) {
        if (epicTask.taskMap.containsKey(epicTaskId) && epicSubTaskIdMap.containsKey(epicTaskId)) {
            subTaskId = epicSubTaskIdMap.get(epicTaskId);
            TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
            System.out.println("Подзадачи эпика " + epicObject.getTaskName() + ":");
            for (int taskId : subTaskId) {
                if (subTask.taskMap.containsKey(taskId)){
                    TaskObject object = subTask.taskMap.get(taskId);
                    System.out.println(object.getTaskName());
                }
            }
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
        }
    }
}