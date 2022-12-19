package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;

import java.util.List;

/**
 * Базовый интерфейс менеджеров по работе с задачами
 */

public interface TaskManager {

    List<Task> getHistory();

    void manageTaskObject(Task object);

    void manageTask(Task taskObject);

    void manageSubTask(SubTask subObject);

    void manageEpicTask(EpicTask epicObject);

    Task getTaskObjectById(int taskId);

    Task getTask(int taskId);

    Task getEpicTask(int taskId);

    Task getSubTask(int taskId);

    List<Task> getOneEpicSubTasks(int epicTaskId);

    List<Task> getAllTypesTasks();

    List<Task> getAllTasks();

    List<Task> getAllEpicTasks();

    List<Task> getAllSubTasks();

    boolean deleteTaskById(int taskId);

    void deleteTask(int taskId);

    void deleteEpicTask(int taskId);

    void deleteSubTask(int taskId);

    boolean deleteAllTasks();

    boolean deleteAllEpicTasks();

    boolean deleteAllSubTasks();

}