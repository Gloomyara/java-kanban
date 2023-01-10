package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.manager.TaskManager;
import ru.mikhailantonov.taskmanager.util.*;
import ru.mikhailantonov.taskmanager.task.*;

public class Main {

    //Доброго времени суток, уважаемый Артем!

    public static void main(String[] args) {
        System.out.println("Поехали!");

        try {
            //считываем
            //TaskManager taskManager = Managers.getDefault();
            TaskManager taskManager = Managers.getDefault(false, "autosave.csv");
            //проверка истории
            System.out.println("Проверка истории: ***" + taskManager.getHistory() + "***");
            //проверка мап
            System.out.println("Проверка мап : " + taskManager.getAllTypesTasks());
            System.out.println("Такой себе сценарий");
            System.out.println();

            //создать 8 задач
            Task taskObject1 = new Task("Задача1", StatusType.NEW, "adsf");
            Task taskObject2 = new Task("Задача2", StatusType.NEW, "fdsa");
            Task taskObject3 = new EpicTask("ЭпикЗадача1", "fddadsf");
            Task taskObject4 = new SubTask("Э1 ПодЗадача1", StatusType.NEW, "asdfsa", 3);
            Task taskObject5 = new SubTask("Э1 ПодЗадача2", StatusType.NEW, "fdfasdf", 3);
            Task taskObject6 = new EpicTask("ЭпикЗадача2", "fsgnbdfg");
            Task taskObject7 = new SubTask("Э2 ПодЗадача1", StatusType.NEW, "asdfsa", 6);
            Task taskObject8 = new SubTask("Э1 ПодЗадача3", StatusType.NEW, "fdfxx", 3);
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
        } catch (ManagerSaveException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
