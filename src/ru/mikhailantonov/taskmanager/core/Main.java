package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.core.TaskManager;
import ru.mikhailantonov.taskmanager.task.*;


import ru.mikhailantonov.taskmanager.manager.Manager;

public class Main {

    //Доброго времени суток, уважаемый Артем!

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();
        //создать 7 задач
        Task taskObject1 = new Task("Задача1", "adsf", StatusType.NEW);
        Task taskObject2 = new Task("Задача2", "fdsa", StatusType.NEW);
        Task taskObject3 = new EpicTask("ЭпикЗадача1", "fddadsf");
        Task taskObject4 = new SubTask(3, "ПодЗадача1", "asdfsa", StatusType.NEW);
        Task taskObject5 = new SubTask(3, "ПодЗадача2", "fdfasdf", StatusType.NEW);
        Task taskObject6 = new EpicTask("ЭпикЗадача2", "fsgnbdfg");
        Task taskObject7 = new SubTask(6, "ПодЗадача1", "asdfsa", StatusType.NEW);
        //распределить по типу
        manager.manageObject(taskObject1);
        manager.manageObject(taskObject2);
        manager.manageObject(taskObject3);
        manager.manageObject(taskObject4);
        manager.manageObject(taskObject5);
        manager.manageObject(taskObject6);
        manager.manageObject(taskObject7);
        //печать всех задач через toString
        for (int i = 1; i < 8; i++) {
            Task object = manager.getObjectById(i);
            System.out.println(object);
        }
        //печать всех задач
        manager.printAllTypesTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = manager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            manager.manageObject(object);
        }

        //печать всех задач
        manager.printAllTypesTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = manager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.DONE);
            }
            manager.manageObject(object);
        }

        //печать всех задач
        manager.printAllTypesTasks();

        //печать задач 1 эпика
        manager.printOneEpicSubTasks(3);

        //удаление 3х задач
        manager.deleteOneTask(1);
        manager.deleteOneTask(6);
        manager.deleteOneTask(4);

        //печать всех задач
        manager.printAllTypesTasks();
    }
}
