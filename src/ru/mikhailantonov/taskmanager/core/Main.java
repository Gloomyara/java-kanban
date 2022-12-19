package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.manager.TaskManager;
import ru.mikhailantonov.taskmanager.util.*;
import ru.mikhailantonov.taskmanager.task.*;

public class Main {

    //Доброго времени суток, уважаемый Артем!


    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();
        //создать 7 задач
        Task taskObject1 = new Task("Задача1", "adsf", StatusType.NEW);
        Task taskObject2 = new Task("Задача2", "fdsa", StatusType.NEW);
        Task taskObject3 = new EpicTask("ЭпикЗадача1", "fddadsf");
        Task taskObject4 = new SubTask(3, "Э1 ПодЗадача1", "asdfsa", StatusType.NEW);
        Task taskObject5 = new SubTask(3, "Э1 ПодЗадача2", "fdfasdf", StatusType.NEW);
        Task taskObject6 = new EpicTask("ЭпикЗадача2", "fsgnbdfg");
        Task taskObject7 = new SubTask(6, "Э2 ПодЗадача1", "asdfsa", StatusType.NEW);
        Task taskObject8 = new SubTask(3, "Э1 ПодЗадача3", "fdfxx", StatusType.NEW);
        //распределить по типу
        taskManager.manageTaskObject(taskObject1);
        taskManager.manageTaskObject(taskObject2);
        taskManager.manageTaskObject(taskObject3);
        taskManager.manageTaskObject(taskObject4);
        taskManager.manageTaskObject(taskObject5);
        taskManager.manageTaskObject(taskObject6);
        taskManager.manageTaskObject(taskObject7);
        taskManager.manageTaskObject(taskObject8);
        //печать всех задач через toString
        for (int i = 1; i < 9; i++) {
            Task object = taskManager.getTaskObjectById(i);
            System.out.println(object);
        }
        //печать всех задач
        System.out.println(taskManager.getAllTypesTasks());
        //история просмотров
        System.out.println("История: ***" + taskManager.getHistory() + "***");

        //обновление статусов задач
        for (int i = 1; i < 9; i++) {
            Task object = taskManager.getTaskObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageTaskObject(object);
        }

        //история просмотров
        System.out.println("История: ***" + taskManager.getHistory() + "***");

        //обновление статусов задач
        for (int i = 1; i < 9; i++) {
            Task object = taskManager.getTaskObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.DONE);
            }
            taskManager.manageTaskObject(object);
        }
        taskManager.getTaskObjectById(7);
        taskManager.getTaskObjectById(6);
        taskManager.getTaskObjectById(5);

        //история просмотров
        System.out.println("История: ***" + taskManager.getHistory() + "***");

        //печать задач 1 эпика
        System.out.println("Подзадачи Эпика: " + taskManager.getTaskObjectById(3).getTaskName() + " : "
                + taskManager.getOneEpicSubTasks(3));

        //удаление 3х задач
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(3);
        taskManager.deleteTaskById(4);
        System.out.println("История: ***" + taskManager.getHistory() + "***");
        //печать всех задач
        System.out.println(taskManager.getAllTypesTasks());

        taskManager.deleteAllTasks();
        System.out.println("История: ***" + taskManager.getHistory() + "***");
        taskManager.deleteAllSubTasks();
        System.out.println("История: ***" + taskManager.getHistory() + "***");
        taskManager.deleteAllEpicTasks();
        taskManager.getHistory();
    }
}
