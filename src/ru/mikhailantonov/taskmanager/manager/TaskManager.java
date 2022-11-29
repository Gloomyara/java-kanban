package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

import java.util.ArrayList;

/**
 * Базовый интерфейс менеджеров по работе с задачами
 */

public interface TaskManager {

    void manageTaskObject(Task object);

    void manageTask(Task taskObject);

    void manageSubTask(SubTask subObject);

    void manageEpicTask(EpicTask epicObject);

    Task getTaskObjectById(int taskId);

    Task getTask(int taskId);

    Task getEpicTask(int taskId);

    Task getSubTask(int taskId);

    ArrayList<Task> getOneEpicSubTasks(int epicTaskId);

    ArrayList<Task> getAllTypesTasks();

    ArrayList<Task> getAllTasks();

    ArrayList<Task> getAllEpicTasks();

    ArrayList<Task> getAllSubTasks();

    void deleteTaskById(int taskId);

    void deleteTask(int taskId);

    void deleteEpicTask(int taskId);

    void deleteSubTask(int taskId);

    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubTasks();

}