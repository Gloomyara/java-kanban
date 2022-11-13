package ru.mikhailantonov.taskmanager.core;

public class TaskManager {

    int id = 1; //было нужно для тестов

    Task task = new Task();
    EpicTask epicTask = new EpicTask();

    //получить задачу по ID
    public TaskObject getObjectById(int taskId) {

        if (task.taskMap.containsKey(taskId)) {
            return task.getOneTask(taskId);
        }
        return epicTask.getOneTask(taskId);
    }

    //обработка входящей задачи
    public void manageTask(TaskObject object) {
        int newId = id;

        Integer epicTaskId = object.getEpicTaskId();
        if (object.getTaskId() == null) {
            object.setTaskId(newId);
        }
        int taskId = object.getTaskId();

        //условие для создания эпика
        if (object.isEpic()) {
            if (!epicTask.taskMap.containsKey(taskId)) {
                epicTask.createNewTask(object);
                newId = id + 1;
            } else {
                epicTask.updateTask(object);
            }
            //условие для создания подзадачи
        } else if (epicTaskId != null) {

            if (epicTask.taskMap.containsKey(epicTaskId)) {
                TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
                if (!epicObject.subTaskMap.containsKey(taskId)) {
                    epicTask.createNewSubTask(object);
                    newId = id + 1;
                } else {
                    epicTask.updateSubTask(object);
                }
            } else {
                System.out.println("Ошибка! такого эпика с таким ID нет.");
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

    //метод для печати всех подзадач 1 эпика
    void printOneEpicSubTasks(int epicTaskId) {
        if (epicTask.taskMap.containsKey(epicTaskId)) {
            TaskObject epicObject = epicTask.taskMap.get(epicTaskId);
            System.out.println("Подзадачи эпика: " + epicObject.getTaskName());
            for (TaskObject subObject : epicObject.subTaskMap.values()) {
                System.out.println(subObject.getTaskName());
            }
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
        }
    }

    //удалить по ID
    public String deleteOneTask(int id) {

        if (task.taskMap.containsKey(id)) {
            return task.deleteOneTask(id);
        }
        return epicTask.deleteOneTask(id);
    }

    //печать всех задач
    public void printAllTasks() {
        System.out.println(task.taskMap);
        System.out.println(epicTask.taskMap);
        /*task.printAllTasks();
        epicTask.printAllTasks();
        epicTask.printAllSubTasks();*/
    }
}