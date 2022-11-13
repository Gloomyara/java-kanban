package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;

class Task {

    protected HashMap<Integer, TaskObject> taskMap = new HashMap<>();

    //печать по типу
    public void printAllTasks() {

        if (!taskMap.isEmpty()) {
            for (TaskObject oneTask : taskMap.values()) {
                System.out.println(oneTask.getTaskName());
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }

    //удалить по типу
    public void deleteAllTasks() {

        if (!taskMap.isEmpty()) {
            taskMap.clear();
            System.out.println("все задачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    //Получить по ID
    public TaskObject getOneTask(int taskId) {
        return taskMap.get(taskId);
    }

    //создать задачу
    public void createNewTask(TaskObject task) {
        int taskId = task.getTaskId();
        task.setTaskCreateDate(Calendar.getInstance());
        task.setTaskUpdateDate(Calendar.getInstance());

        if (!taskMap.containsKey(taskId)) {
            taskMap.put(taskId, task);
        } else {
            System.out.println("Ошибка! Задача с таким ID уже есть");
        }
    }

    //обновить задачу
    public void updateTask(TaskObject task) {

        int taskId = task.getTaskId();

        if (taskMap.containsKey(taskId)) {
            TaskObject oneTask = taskMap.get(taskId);
            oneTask.setTaskStatus(task.getTaskStatus());
            oneTask.setTaskName(task.getTaskName());
            oneTask.setTaskDescription(task.getTaskDescription());
            oneTask.setTaskStatus(task.getTaskStatus());

            if (oneTask.getTaskStatus() == StatusType.DONE) {
                oneTask.setCloseDate(Calendar.getInstance());
            } else {
                oneTask.setTaskUpdateDate(Calendar.getInstance());
            }
        } else {
            System.out.println("Такой задачи нет");
        }
    }
    //Удалить по ID
    public String deleteOneTask(int id) {
        taskMap.remove(id);
        return "Задача ID: " + id + " удалена.";
    }
}