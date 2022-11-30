package ru.mikhailantonov.taskmanager.core;

import ru.mikhailantonov.taskmanager.manager.TaskManager;
import ru.mikhailantonov.taskmanager.util.*;
import ru.mikhailantonov.taskmanager.task.*;

public class Main {

    //Доброго времени суток, уважаемый Артем!
    //В Managers метод getDefaultHistory(), не потерянный, он всегда там был т.к. в тз сказано:
    /*
     *Добавьте в служебный класс Managers статический метод HistoryManager getDefaultHistory().
     *Он должен возвращать объект InMemoryHistoryManager — историю просмотров.
     */
    //То есть следуя логике написанного в InMemoryTaskManager метод getHistory() не нужен...
    //Или же мне нужно передать лист из InMemoryHistoryManager в InMemoryTaskManager, через getDefaultHistory(),
    //а уже в Main вызывать getHistory из InMemoryTaskManager, который вернет getDefaultHistory()?
    //Что-то не понимаю ни чего, как правильно в итоге?)))
    //В любом случае, видимо, зря удалил метод из InMemoryTaskManager,
    //поскольку не очень понимаю зачем нужно 2 таких метода...
    //Добавил условие для создания менеджеров, так-то 1 из них используется в getDefaultHistory();
    //Можно обойтись без этого, сделал как раз без использования этих полей, в ТЗ же не сказано,
    //что getDefaultHistory() должен быть без параметров?
    //Спасибо за наставления!

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
        taskManager.manageTaskObject(taskObject1);
        taskManager.manageTaskObject(taskObject2);
        taskManager.manageTaskObject(taskObject3);
        taskManager.manageTaskObject(taskObject4);
        taskManager.manageTaskObject(taskObject5);
        taskManager.manageTaskObject(taskObject6);
        taskManager.manageTaskObject(taskObject7);
        //печать всех задач через toString
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getTaskObjectById(i);
            System.out.println(object);
        }
        //печать всех задач
        taskManager.getAllTypesTasks();
        //история просмотров
        System.out.println("История: ***" + Managers.getDefaultHistory(taskManager) + "***");

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            Task object = taskManager.getTaskObjectById(i);
            if (!(object instanceof EpicTask)) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageTaskObject(object);
        }

        //история просмотров
        System.out.println("История: ***" + Managers.getDefaultHistory(taskManager) + "***");

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
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
        System.out.println("История: ***" + Managers.getDefaultHistory(taskManager) + "***");

        //печать задач 1 эпика
        System.out.println("Подзадачи Эпика: " + taskManager.getTaskObjectById(3).getTaskName() + " : "
                + taskManager.getOneEpicSubTasks(3));

        //удаление 3х задач
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(6);
        taskManager.deleteTaskById(4);

        //печать всех задач
        System.out.println(taskManager.getAllTypesTasks());
    }
}
