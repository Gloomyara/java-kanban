package ru.mikhailantonov.taskmanager.core;

public class Main {

    //Доброго времени суток, уважаемый Артем!

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        //создать 7 задач
        Task taskObject1 = new Task("Задача1", "adsf", StatusType.NEW);
        Task taskObject2 = new Task("Задача2", "fdsa", StatusType.NEW);
        Task taskObject3 = new EpicTask("ЭпикЗадача1", "fddadsf");
        Task taskObject4 = new SubTask(3, "ПодЗадача1", "asdfsa", StatusType.NEW);
        Task taskObject5 = new SubTask(3, "ПодЗадача2", "fdfasdf", StatusType.NEW);
        Task taskObject6 = new EpicTask("ЭпикЗадача2", "fsgnbdfg");
        Task taskObject7 = new SubTask(6, "ПодЗадача1", "asdfsa", StatusType.NEW);
        //распределить по типу
        taskManager.manageObject(taskObject1);
        taskManager.manageObject(taskObject2);
        taskManager.manageObject(taskObject3);
        taskManager.manageObject(taskObject4);
        taskManager.manageObject(taskObject5);
        taskManager.manageObject(taskObject6);
        taskManager.manageObject(taskObject7);
        //печать всех задач через toString
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getObjectById(i);
            System.out.println(object);
        }
        //печать всех задач
        taskManager.printAllTypesTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageObject(object);
        }

        //печать всех задач
        taskManager.printAllTypesTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.DONE);
            }
            taskManager.manageObject(object);
        }

        //печать всех задач
        taskManager.printAllTypesTasks();

        //печать задач 1 эпика
        taskManager.printOneEpicSubTasks(3);

        //удаление 2х задач
        taskManager.deleteOneTask(1);
        taskManager.deleteOneTask(6);

        //печать всех задач
        taskManager.printAllTypesTasks();
    }
}
