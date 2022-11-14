package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

public class TaskManager {

    int id = 1; //было нужно для тестов
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private HashMap<Integer, Integer> epicSubTaskIdMap = new HashMap<>();


    //обработка входящей задачи
    public void manageObject(Task object) {

        if (object.getTaskId() == null) {
            object.setTaskId(id);
        }

        //условие для создания эпика
        if (object instanceof EpicTask) {
            manageEpicTask((EpicTask) object);
            //условие для создания подзадачи
        } else if (object instanceof SubTask) {
            manageSubTask((SubTask) object);
            //условие для создания задачи
        } else {
            manageTask(object);
        }
    }

    public void manageEpicTask(EpicTask epicObject) {

        int taskId = epicObject.getTaskId();

        if (!epicTaskMap.containsKey(taskId)) {

            epicObject.setTaskStatus(epicObject.epicStatusType());
            epicObject.setTaskCreateDate(Calendar.getInstance());
            epicObject.setTaskUpdateDate(Calendar.getInstance());
            epicTaskMap.put(taskId, epicObject);
            id = id + 1;
        } else {

            EpicTask object = epicTaskMap.get(taskId);
            object.setTaskName(epicObject.getTaskName());
            object.setTaskDescription(epicObject.getTaskDescription());
            object.setTaskStatus(epicObject.epicStatusType());

            if (object.getTaskStatus() == StatusType.DONE) {
                object.setCloseDate(Calendar.getInstance());
            } else {
                object.setTaskUpdateDate(Calendar.getInstance());
            }
        }
    }

    public void manageSubTask(SubTask subObject) {

        int taskId = subObject.getTaskId();
        int epicTaskId = subObject.getEpicTaskId();
        if (!epicTaskMap.containsKey(epicTaskId)) {
            System.out.println("Ошибка! эпик задачи с таким ID нет.");
        } else {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);

            if (!epicObject.subTaskMap.containsKey(taskId)) {

                subObject.setTaskStatus(StatusType.NEW);
                subObject.setTaskCreateDate(Calendar.getInstance());
                subObject.setTaskUpdateDate(Calendar.getInstance());
                epicObject.subTaskMap.put(taskId, subObject);
                epicSubTaskIdMap.put(taskId, epicTaskId);
                id = id + 1;
            } else {

                SubTask object = epicObject.subTaskMap.get(taskId);
                object.setTaskName(subObject.getTaskName());
                object.setTaskDescription(subObject.getTaskDescription());
                object.setTaskStatus(subObject.getTaskStatus());

                if (object.getTaskStatus() == StatusType.DONE) {
                    object.setCloseDate(Calendar.getInstance());
                } else {
                    object.setTaskUpdateDate(Calendar.getInstance());
                }
                epicObject.setTaskStatus(epicObject.epicStatusType());
            }
        }
    }

    public void manageTask(Task taskObject) {

        int taskId = taskObject.getTaskId();

        if (!taskMap.containsKey(taskId)) {

            taskObject.setTaskStatus(StatusType.NEW);
            taskObject.setTaskCreateDate(Calendar.getInstance());
            taskObject.setTaskUpdateDate(Calendar.getInstance());
            taskMap.put(taskId, taskObject);
            id = id + 1;
        } else {

            Task object = taskMap.get(taskId);
            object.setTaskName(taskObject.getTaskName());
            object.setTaskDescription(taskObject.getTaskDescription());
            object.setTaskStatus(taskObject.getTaskStatus());

            if (object.getTaskStatus() == StatusType.DONE) {
                object.setCloseDate(Calendar.getInstance());
            } else {
                object.setTaskUpdateDate(Calendar.getInstance());
            }
        }
    }

    //метод для печати всех подзадач 1 эпика
    public void printOneEpicSubTasks(int epicTaskId) {
        if (epicTaskMap.containsKey(epicTaskId)) {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);
            System.out.println("Подзадачи эпика: " + epicObject.getTaskName());
            for (SubTask subObject : epicObject.subTaskMap.values()) {
                System.out.println(subObject.getTaskName());
            }
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
        }
    }

    //получить задачу по ID
    public Task getObjectById(int taskId) {

        if (taskMap.containsKey(taskId)) {
            return taskMap.get(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            return epicTaskMap.get(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            int epicTaskId = epicSubTaskIdMap.get(taskId);
            EpicTask epicTask = epicTaskMap.get(epicTaskId);
            return epicTask.subTaskMap.get(taskId);
        }
        return null;
    }

    //удалить по ID
    public void deleteOneTask(int taskId) {

        if (taskMap.containsKey(taskId)) {
            taskMap.remove(taskId);
            System.out.println("Задача под номером: " + taskId + " удалена.");
        } else if (epicTaskMap.containsKey(taskId)) {
            epicTaskMap.remove(taskId);
            System.out.println("Эпик под номером: " + taskId + " и все его подзадачи удалены.");
        } else if (epicSubTaskIdMap.containsKey(taskId)) {

            int epicTaskId = epicSubTaskIdMap.get(taskId);
            EpicTask epicObject = epicTaskMap.get(epicTaskId);
            epicObject.subTaskMap.remove(taskId);
            epicSubTaskIdMap.remove(taskId);
            System.out.println("Подзадача эпика: " + epicTaskId + ", под номером: " + taskId + " удалена.");
        } else {
            System.out.println("Задачи с таким ID нет");
        }
    }

    //печать всех задач
    public void printAllTypesTasks() {
        System.out.println(taskMap);
        System.out.println(epicTaskMap);
    }

    //печать задач
    public void printAllTasks() {

        if (!taskMap.isEmpty()) {
            for (Task object : taskMap.values()) {
                System.out.println(object.taskName);
            }
        } else {
            System.out.println("Ошибка! не найдено задач!");
        }
    }

    //печать эпиков
    public void printAllEpicTasks() {

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                System.out.println(epicObject.taskName);
            }
        } else {
            System.out.println("Ошибка! не найдено эпиков'!");
        }
    }

    //печать подзадач
    public void printAllSubTasks() {

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                System.out.println("Подзадачи эпика: " + epicObject.taskName);
                if (!epicObject.subTaskMap.isEmpty()) {
                    for (SubTask subObject : epicObject.subTaskMap.values())
                        System.out.println("Подазадача: " + subObject.taskName);
                } else {
                    System.out.println("Нет подзадач");
                }
            }
        } else {
            System.out.println("Нет подзадач");
        }
    }

    //удалить все задачи
    public void deleteAllTasks() {

        if (!taskMap.isEmpty()) {
            taskMap.clear();
            System.out.println("все задачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    //удалить все эпики
    public void deleteAllEpicTasks() {

        if (!epicTaskMap.isEmpty()) {
            epicTaskMap.clear();
            System.out.println("все эпики удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    //удалить все подзадачи
    public void deleteAllSubTasks() {

        if (!epicSubTaskIdMap.isEmpty()) {
            epicSubTaskIdMap.clear();
            for (EpicTask epicObject : epicTaskMap.values()) {
                if (!epicObject.subTaskMap.isEmpty()) {
                    epicObject.subTaskMap.clear();
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " все подзадачи удалены");
                } else {
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " Нечего удалять");
                }
            }
        } else {
            System.out.println("Нечего удалять");
        }
    }
}