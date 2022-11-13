package ru.mikhailantonov.taskmanager.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

class EpicTask extends Task {

    HashMap<Integer, Integer> epicSubTaskIdMap = new HashMap<>();

    @Override
    public void createNewTask(TaskObject epicObject) {

        int taskId = epicObject.getTaskId();
        epicObject.setTaskCreateDate(Calendar.getInstance());
        epicObject.setTaskUpdateDate(Calendar.getInstance());
        epicObject.setTaskStatus(epicStatusType(taskId));
        epicObject.subTaskMap = new HashMap<>();

        if (!taskMap.containsKey(taskId)) {
            taskMap.put(taskId, epicObject);
        } else {
            System.out.println("Ошибка! Задача с таким ID уже есть");
        }
    }

    @Override
    public void updateTask(TaskObject task) {

        int taskId = task.getTaskId();

        if (taskMap.containsKey(taskId)) {

            TaskObject epicObject = taskMap.get(taskId);
            epicObject.setTaskStatus(task.getTaskStatus());
            epicObject.setTaskName(task.getTaskName());
            epicObject.setTaskDescription(task.getTaskDescription());
            epicObject.setTaskStatus(epicStatusType(taskId));

            if (epicObject.getTaskStatus() == StatusType.DONE) {

                epicObject.setCloseDate(Calendar.getInstance());
            } else {

                epicObject.setTaskUpdateDate(Calendar.getInstance());
            }

        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public void createNewSubTask(TaskObject object) {
        int taskId = object.getTaskId();
        int epicTaskId = object.getEpicTaskId();
        TaskObject epicObject = taskMap.get(epicTaskId);
        object.setTaskCreateDate(Calendar.getInstance());
        object.setTaskUpdateDate(Calendar.getInstance());
        if (!epicObject.subTaskMap.containsKey(taskId)) {
            epicSubTaskIdMap.put(taskId, epicTaskId);
            epicObject.subTaskMap.put(taskId, object);
        } else {
            System.out.println("Ошибка! Задача с таким ID уже есть");
        }
        epicObject.setTaskStatus(epicStatusType(epicTaskId));
    }

    public void updateSubTask(TaskObject task) {
        int epicTaskId = task.getEpicTaskId();
        int taskId = task.getTaskId();

        if (taskMap.containsKey(epicTaskId)) {

            TaskObject epicObject = taskMap.get(epicTaskId);
            TaskObject object = epicObject.subTaskMap.get(taskId);
            object.setTaskStatus(task.getTaskStatus());
            object.setTaskName(task.getTaskName());
            object.setTaskDescription(task.getTaskDescription());
            object.setTaskStatus(task.getTaskStatus());

            if (object.getTaskStatus() == StatusType.DONE) {

                object.setCloseDate(Calendar.getInstance());

            } else {

                object.setTaskUpdateDate(Calendar.getInstance());
            }
            epicObject.setTaskStatus(epicStatusType(epicTaskId));
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    //печать подзадач
    public void printAllSubTasks() {

        if (!taskMap.isEmpty()) {
            for (TaskObject epicObject : taskMap.values()) {
                System.out.println("Подзадачи эпика: " + epicObject.getTaskName());
                for (TaskObject subObject : epicObject.subTaskMap.values()) {
                    System.out.println(subObject.getTaskName());
                }
            }
        } else {
            System.out.println("Ошибка! нет задач");
        }
    }

    //удаление по ИД
    @Override
    public String deleteOneTask(int id) {

        if (epicSubTaskIdMap.containsKey(id)) {
            int epicTaskId = epicSubTaskIdMap.get(id);
            TaskObject epicObject = taskMap.get(epicTaskId);
            epicObject.subTaskMap.remove(id);
            return "Подзадача ID: " + id + " удалена.";

        } else if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            return "Эпик задача ID: " + id + " и все ее подзадачи удалены.";
        }
        return "Задачи под таким ID нет.";
    }

    //Получить по ID
    @Override
    public TaskObject getOneTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            return taskMap.get(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            int epicTaskId = epicSubTaskIdMap.get(taskId);
            TaskObject epicObject = taskMap.get(epicTaskId);
            return epicObject.subTaskMap.get(taskId);
        }
        return null;
    }

    // ниже методы для статуса эпика оба варианта рабочие
    /*public StatusType epicStatusType(int epicTaskId) {

        ArrayList<Integer> check1 = new ArrayList<>();
        ArrayList<Integer> check2 = new ArrayList<>();
        StatusType status = null;
        if (taskMap.containsKey(epicTaskId)) {
            TaskObject epicObject = taskMap.get(epicTaskId);

            for (int i : epicObject.subTaskMap.keySet()) {
                TaskObject object = epicObject.subTaskMap.get(i);
                if (object.getTaskStatus().equals(StatusType.NEW) || object.getTaskStatus() == null) {
                    check1.add(i);
                } else if (object.getTaskStatus().equals(StatusType.DONE)) {
                    check2.add(i);
                }
            }
            if (check1.size() == epicObject.subTaskMap.size()) {
                status = StatusType.NEW;
            } else if (check2.size() == epicObject.subTaskMap.size()) {
                status = StatusType.DONE;
            } else {
                status = StatusType.IN_PROGRESS;
            }
        }
        return status;
    }*/
//как лучше?
    public StatusType epicStatusType(int taskId) {

        StatusType status = null;
        if (!taskMap.containsKey(taskId)) {
            if (epicSubTaskIdMap.containsKey(taskId)) {
                int epicTaskId = epicSubTaskIdMap.get(taskId);
                status = epicStatus(epicTaskId);
            }
        } else {
            status = epicStatus(taskId);
        }
        return status;
    }

    public StatusType epicStatus(int taskId) {
        ArrayList<Integer> check1 = new ArrayList<>();
        ArrayList<Integer> check2 = new ArrayList<>();
        StatusType status;
        TaskObject epicTask = taskMap.get(taskId);
        if (epicTask.subTaskMap.isEmpty()) {
            return StatusType.NEW;
        } else {

            for (int i : epicTask.subTaskMap.keySet()) {
                TaskObject object = epicTask.subTaskMap.get(i);
                if (object.getTaskStatus().equals(StatusType.NEW)) {
                    check1.add(i);
                } else if (object.getTaskStatus().equals(StatusType.DONE)) {
                    check2.add(i);
                }
            }
            if (check1.size() == epicTask.subTaskMap.size()) {
                status = StatusType.NEW;
            } else if (check2.size() == epicTask.subTaskMap.size()) {
                status = StatusType.DONE;
            } else {
                status = StatusType.IN_PROGRESS;
            }
        }
        return status;
    }
}


