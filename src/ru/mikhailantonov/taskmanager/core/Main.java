package ru.mikhailantonov.taskmanager.core;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        TaskObject taskObject1 = new TaskObject(null, false, "Задача1", "adsf", StatusType.NEW);
        TaskObject taskObject2 = new TaskObject(null, false, "Задача2", "fdsa", StatusType.NEW);
        TaskObject taskObject3 = new TaskObject(null, true, "ЭпикЗадача1", "fddadsf", null);
        TaskObject taskObject4 = new TaskObject(3, false, "ПодЗадача1", "asdfsa", StatusType.NEW);
        TaskObject taskObject5 = new TaskObject(3, false, "ПодЗадача2", "fdfasdf", StatusType.NEW);
        TaskObject taskObject6 = new TaskObject(null, true, "ЭпикЗадача2", "fsgnbdfg", null);
        TaskObject taskObject7 = new TaskObject(6, false, "ПодЗадача1", "asdfsa", StatusType.NEW);

        taskManager.manageTask(taskObject1);
        taskManager.manageTask(taskObject2);
        taskManager.manageTask(taskObject3);
        taskManager.manageTask(taskObject4);
        taskManager.manageTask(taskObject5);
        taskManager.manageTask(taskObject6);
        taskManager.manageTask(taskObject7);

        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            System.out.println(object);
        }
        System.out.println(taskManager.task.taskMap);
        System.out.println(taskManager.epicTask.taskMap);
        System.out.println(taskManager.subTask.taskMap);

        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            if (!object.isEpic()) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageTask(object);
        }
        System.out.println(taskManager.task.taskMap);
        System.out.println(taskManager.epicTask.taskMap);
        System.out.println(taskManager.subTask.taskMap);

        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            if (!object.isEpic()) {
                object.setTaskStatus(StatusType.DONE);
            }
            taskManager.manageTask(object);
        }
        System.out.println(taskManager.task.taskMap);
        System.out.println(taskManager.subTask.taskMap);
        System.out.println(taskManager.epicTask.taskMap);
        taskManager.printOneEpicSubTasks(3);
    }
}
