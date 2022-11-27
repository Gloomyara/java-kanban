package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

/** Базовый интерфейс менеджеров по работе с задачами */

public interface TaskManager {
    String getHistory();

    void manageObject(Task object);

    void manageTask(Task taskObject);

    void manageSubTask(SubTask subObject);

    void manageEpicTask(EpicTask epicObject);

    Task getObjectById(int taskId);

    Task getTask(int taskId);

    Task getEpicTask(int taskId);

    Task getSubTask(int taskId);

    void deleteTaskById(int taskId);

    void deleteTask(int taskId);

    void deleteEpicTask(int taskId);

    void deleteSubTask(int taskId);

    void printOneEpicSubTasks(int epicTaskId);

    void printAllTypesTasks();

    void printAllTasks();

    void printAllEpicTasks();

    void printAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();


}