package ru.mikhailantonov.taskmanager.core;

public class Main {

    //Доброго времени суток, уважаемый Артем!
    //Не знаю как описать это ДЗ, с 1й стороны ради удовлетворения ТЗ приходиться делать
    //какие-то костыльные и бесполезные методы, с другой пытаешься сделать что-то нормальное...

    //Эта версия уже 3, изначально отправил 2, но успел переделать до ее проверки
    //В 3 версии получилось нечто среднее между 1 и 2, в процессе создания 3 чуть было не начал делать уже по сути 4
    //На данный момент шаблон TaskObject включает в себя все поля для удобства нашего костыльного теста в Main
    //Идея 4 версии была в 2х доп классах EpicTaskObject и SubTaskObject, дабы вынести уникальные для них поля,
    //но очень быстро наткнулся на то, что везде все надо будет переделывать и особенно в Main.
    //В нужном ключе мыслю по поводу 4 версии?
    //Так что доделал 3 версию, дабы успеть обновить до ревью

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        //создать 7 задач
        TaskObject taskObject1 = new TaskObject(null, false, "Задача1", "adsf", StatusType.NEW);
        TaskObject taskObject2 = new TaskObject(null, false, "Задача2", "fdsa", StatusType.NEW);
        TaskObject taskObject3 = new TaskObject(null, true, "ЭпикЗадача1", "fddadsf", null);
        TaskObject taskObject4 = new TaskObject(3, false, "ПодЗадача1", "asdfsa", StatusType.NEW);
        TaskObject taskObject5 = new TaskObject(3, false, "ПодЗадача2", "fdfasdf", StatusType.NEW);
        TaskObject taskObject6 = new TaskObject(null, true, "ЭпикЗадача2", "fsgnbdfg", null);
        TaskObject taskObject7 = new TaskObject(6, false, "ПодЗадача1", "asdfsa", StatusType.NEW);
        //распределить по типу
        taskManager.manageTask(taskObject1);
        taskManager.manageTask(taskObject2);
        taskManager.manageTask(taskObject3);
        taskManager.manageTask(taskObject4);
        taskManager.manageTask(taskObject5);
        taskManager.manageTask(taskObject6);
        taskManager.manageTask(taskObject7);
        //печать всех задач через toString
        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            System.out.println(object);
        }
        //печать всех задач
        taskManager.printAllTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            if (!object.isEpic()) {
                object.setTaskStatus(StatusType.IN_PROGRESS);
            }
            taskManager.manageTask(object);
        }

        //печать всех задач
        taskManager.printAllTasks();

        //обновление статусов задач
        for (int i = 1; i < 8; i++) {
            TaskObject object = taskManager.getObjectById(i);
            if (!object.isEpic()) {
                object.setTaskStatus(StatusType.DONE);
            }
            taskManager.manageTask(object);
        }

        //печать всех задач
        taskManager.printAllTasks();

        //печать задач 1 эпика
        taskManager.printOneEpicSubTasks(3);

        //удаление 2х задач
        System.out.println(taskManager.deleteOneTask(1));
        System.out.println(taskManager.deleteOneTask(6));

        //печать всех задач
        taskManager.printAllTasks();
    }
}
