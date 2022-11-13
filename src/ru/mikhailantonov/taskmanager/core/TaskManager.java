package ru.mikhailantonov.taskmanager.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class TaskManager {

    int id = 1; //было нужно для тестов

    Task task = new Task();
    EpicTask epicTask = new EpicTask();
    SubTask subTask = new SubTask();
    private ArrayList<Integer> subTaskIdList;
    private HashMap<Integer, ArrayList<Integer>> epicSubTaskIdMap = new HashMap<>();

    //удаление всех эпиков и подзадач
    public void DeleteAllEpicAndSub() {
        subTask.deleteAllTasks();
        epicTask.deleteAllTasks();
    }

    //получить задачу по ID
    public TaskObject getObjectById(int taskId) {

        if (task.taskMap.containsKey(taskId)) {
            return task.taskMap.get(taskId);
        } else if (epicTask.taskMap.containsKey(taskId)) {
            return epicTask.taskMap.get(taskId);
        } else {
            return subTask.taskMap.get(taskId);
        }
    }

    //обработка входящей задачи
    public void manageTask(TaskObject object) {
        int newId = id;
        subTaskIdList = new ArrayList<>();

        Integer epicTaskId = object.getEpicTaskId();
        if (object.getTaskId() == null) {
            object.setTaskId(newId);
        }
        int taskId = object.getTaskId();

        //условие для создания эпика
        if (object.isEpic()) {
            if (!epicTask.taskMap.containsKey(taskId)) {
                object.setTaskId(taskId);
                object.setTaskStatus(epicStatusType(taskId));
                epicTask.createNewTask(object);
                newId = id + 1;
                epicSubTaskIdMap.put(taskId, subTaskIdList);
            } else {
                epicTask.updateTask(object);
            }
            //условие для создания подзадачи
        } else if (epicTaskId != null) {
            if (!subTask.taskMap.containsKey(taskId)) {
                object.setTaskId(taskId);
                subTask.createNewTask(object);
                newId = id + 1;
                if (epicSubTaskIdMap.get(epicTaskId) != null) subTaskIdList = epicSubTaskIdMap.get(epicTaskId);
                subTaskIdList.add(taskId);
                epicSubTaskIdMap.put(epicTaskId, subTaskIdList);

            } else {
                subTask.updateTask(object);
                TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
                epicObject.setTaskStatus(epicStatusType(epicTaskId));
            }
            //условие для создания просто задачи
        } else {

            if (!task.taskMap.containsKey(taskId)) {
                task.createNewTask(object);
                newId = id + 1;
            } else {
                task.updateTask(object);
            }
        }
        id = newId;
    }

    //метод для статуса эпика
    public StatusType epicStatusType(int epicTaskId) {

        ArrayList<Integer> check1 = new ArrayList<>();
        ArrayList<Integer> check2 = new ArrayList<>();
        StatusType status;
        if (epicSubTaskIdMap.get(epicTaskId) == null) {
            return StatusType.NEW;
        } else {
            subTaskIdList = epicSubTaskIdMap.get(epicTaskId);
            for (int i : subTaskIdList) {
                TaskObject object = subTask.taskMap.get(i);
                if (object.getTaskStatus().equals(StatusType.NEW) || object.getTaskStatus() == null) {
                    check1.add(i);
                } else if (object.getTaskStatus().equals(StatusType.DONE)) {
                    check2.add(i);
                }
            }

            if (check1.size() == subTaskIdList.size()) {
                status = StatusType.NEW;
            } else if (check2.size() == subTaskIdList.size()) {
                status = StatusType.DONE;
            } else {
                status = StatusType.IN_PROGRESS;
            }
        }
        return status;
    }

    //метод для печати всех подзадач 1 эпика
    void printOneEpicSubTasks(int epicTaskId) {
        if (epicTask.taskMap.containsKey(epicTaskId) && epicSubTaskIdMap.containsKey(epicTaskId)) {
            subTaskIdList = epicSubTaskIdMap.get(epicTaskId);
            TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
            System.out.println("Подзадачи эпика " + epicObject.getTaskName() + ":");
            for (int taskId : subTaskIdList) {
                if (subTask.taskMap.containsKey(taskId)) {
                    TaskObject object = subTask.taskMap.get(taskId);
                    System.out.println(object.getTaskName());
                }
            }
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
        }
    }

    //удалить по ID
    public String deleteOneTask(int id) {

        if (task.taskMap.containsKey(id)) {
            task.taskMap.remove(id);
            return "Задача ID: " + id + " удалена.";
        } else if (subTask.taskMap.containsKey(id)) {
            subTask.taskMap.remove(id);
            return "Подзадача ID: " + id + " удалена.";
        } else if (epicTask.taskMap.containsKey(id)) {
            if (epicSubTaskIdMap.containsKey(id)) {
                subTaskIdList = epicSubTaskIdMap.get(id);
                System.out.println("Эпик ID:" + id);
                System.out.print("Подзадачи - ");
                for (int i : subTaskIdList) {
                    subTask.taskMap.remove(id);
                    System.out.print("ID: " + i + ".");
                }
                System.out.println(" Удалены");

                epicSubTaskIdMap.remove(id);

            } else {
                return "Удаление подзадач эпика c ID:" + id + " не удалось";
            }
            epicTask.taskMap.remove(id);
            return "Эпик задача удалена";
        }
        return "Задачи под таким ID нет.";
    }
}