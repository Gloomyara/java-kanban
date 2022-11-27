package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.manager.TaskManager;
import ru.mikhailantonov.taskmanager.util.*;
import ru.mikhailantonov.taskmanager.task.*;

public class Main {

    //Доброго времени суток, уважаемый Артем!
    //Добавил пакет util, не знаю правильно ли сделал или нет.
    //В целом, просто сделал то что просили в ТЗ.
    //Если подскажешь, каким образом и как, передавать параметры в Managers,
    //дабы он мог создать нужный нам менеджер, буду признателен.
    //Поскольку сам думал над этим с пятницы, и с сохранением статик методов, кроме как сделать метод
    /*
    public static createManager(TaskManager t){
    this.t = t;
    }
    И создавать в Main через Managers.createManager(new InMemoryHistoryManager);
    ничего не получилось...
     */
    //Написал в воскресенье наставнице, она мне ответила: мол да тебе и не надо ничего делать,
    //т.к. менеджер всего 1, просто создай и верни новый объект...

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();
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
        //история просмотров
        System.out.println("История: ***" + Managers.getDefaultHistory() + "***");

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageObject(object);
        }

        //печать всех задач
        //Managers.getDefault().printAllTypesTasks();
        //история просмотров
        System.out.println("История: ***" + Managers.getDefaultHistory() + "***");

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.DONE);
            }
            taskManager.manageObject(object);
        }
        taskManager.getObjectById(7);
        taskManager.getObjectById(6);
        taskManager.getObjectById(5);

        //печать всех задач
        //Managers.getDefault().printAllTypesTasks();
        //история просмотров
        System.out.println("История: ***" + Managers.getDefaultHistory() + "***");

        //печать задач 1 эпика
        taskManager.printOneEpicSubTasks(3);

        //удаление 3х задач
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(6);
        taskManager.deleteTaskById(4);

        //печать всех задач
        taskManager.printAllTypesTasks();
    }
}
