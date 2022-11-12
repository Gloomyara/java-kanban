package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;


public class TaskManager {
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTaskList = new HashMap<>();
    private HashMap<Integer, Integer> subTaskId = new HashMap<>();

    int id = 0;

    public int createNewTask(Task task) {
        int taskId = task.taskId;
        if (!taskList.containsKey(taskId)) {
            taskList.put(taskId, task);
        } else {
            System.out.println("Такая задача уже есть");
            return taskId;
        }
        return taskId + 1;
    }

    public int createNewSubTask(SubTask task) {
        int epicId = task.epicId;
        int taskId = task.taskId;

        if (epicTaskList.containsKey(epicId)) {
            EpicTask oneTask = epicTaskList.get(epicId);
            System.out.println("Эпик " + oneTask.getTaskName() + "на связи");

            oneTask.addSubTask(task);
            subTaskId.put(taskId, epicId);

        } else {
            System.out.println("Такая задача уже есть");
            return taskId;
        }
        return taskId + 1;
    }

    public int createNewEpicTask(EpicTask task) {
        int taskId = task.taskId;
        if (!epicTaskList.containsKey(taskId)) {

            epicTaskList.put(taskId, task);
        } else {
            System.out.println("Такая задача уже есть");
            return taskId;
        }
        return taskId + 1;
    }

    public void refreshTask(Task task) {

        int taskId = task.taskId;

        if (taskList.containsKey(taskId)) {

            Task oneTask = taskList.get(task.taskId);
            oneTask.taskStatus = task.taskStatus;
            oneTask.taskName = task.taskName;
            oneTask.taskDescription = task.taskDescription;
            //oneTask.setTaskAppoint();

            if (oneTask.taskStatus == StatusType.DONE) {

                oneTask.setCloseDate(Calendar.getInstance());
            } else {

                oneTask.setTaskUpdateDate(Calendar.getInstance());
            }

        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public void refreshSubTask(SubTask task) {

        int epicId = task.epicId;
        int taskId = task.taskId;

        if (epicTaskList.containsKey(epicId)) {

            EpicTask epicTask = epicTaskList.get(epicId);
            SubTask subTask = epicTask.getSubTasks().get(taskId);

            subTask.taskStatus = task.taskStatus;
            if (subTask.taskStatus == StatusType.DONE) {

                subTask.setCloseDate(Calendar.getInstance());
            } else {

                subTask.setTaskUpdateDate(Calendar.getInstance());
            }
            subTask.taskName = task.taskName;
            subTask.taskDescription = task.taskDescription;
            //subTask.setTaskAppoint();
            epicTask.taskStatus = epicTask.epicStatusType();

            if (epicTask.taskStatus == StatusType.DONE) {

                epicTask.setCloseDate(Calendar.getInstance());
            } else {

                epicTask.setTaskUpdateDate(Calendar.getInstance());
            }
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public void refreshEpicTask(EpicTask task) {

        int taskId = task.taskId;

        if (epicTaskList.containsKey(taskId)) {

            EpicTask epicTask = epicTaskList.get(taskId);
            epicTask.taskStatus = task.taskStatus;
            epicTask.taskName = task.taskName;
            epicTask.taskDescription = task.taskDescription;
            epicTask.setTaskUpdateDate(Calendar.getInstance());

        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public String deleteOneTask(int id) {
        if (subTaskId.containsKey(id)) {
            EpicTask oneTask = (EpicTask) taskList.get(subTaskId.get(id));
            oneTask.deleteOneTask(id);
            return "Задача удалена.";
        } else if (taskList.containsKey(id)) {
            taskList.remove(id);
            return "Задача удалена.";
        } else if (epicTaskList.containsKey(id)) {
            epicTaskList.remove(id);
            return "Задача удалена.";
        }
        return "Задачи под таким ID нет.";
    }

    public void printAllTasks() {

        if (!taskList.isEmpty()) {
            for (Task oneTask : taskList.values()) {
                System.out.println(oneTask.taskName);
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }
    public void printAllSubTasks() {

        if (!epicTaskList.isEmpty()) {
            for (EpicTask oneTask : epicTaskList.values()) {

                System.out.println("Подзадачи эпика - " + oneTask.taskName + ":");
                oneTask.printAllTasks();
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }

    public void printAllEpicTasks() {

        if (!epicTaskList.isEmpty()) {
            for (EpicTask oneTask : epicTaskList.values()) {
                System.out.println(oneTask.taskName);
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }

    public void deleteAllTasks(){

        if (!taskList.isEmpty()){
            taskList.clear();
            System.out.println("все задачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    public void deleteAllSubTasks(){

        if (!epicTaskList.isEmpty()) {
            for (EpicTask oneTask : epicTaskList.values()) {
                oneTask.deleteAllSubTasks();
            }
            System.out.println("все подзадачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }
    public void deleteAllEpicTasks(){

        if (!epicTaskList.isEmpty()){
            epicTaskList.clear();
            System.out.println("все эпик задачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }
    public Task getOneTask(int taskId) {
        
    }
}
