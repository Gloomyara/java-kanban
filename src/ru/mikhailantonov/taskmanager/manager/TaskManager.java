package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.TimeStampsCrossingException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 * Базовый интерфейс менеджеров по работе с задачами
 */

public interface TaskManager {

    List<Task> getHistory();

    void manageTaskObject(Task object) throws TimeStampsCrossingException;

    void manageTask(Task taskObject) throws TimeStampsCrossingException;

    void manageSubTask(SubTask subObject) throws TimeStampsCrossingException;

    void manageEpicTask(EpicTask epicObject) throws TimeStampsCrossingException;

    //получить задачу по ID
    Task getTaskObjectById(Integer taskId);

    Task getTask(Integer taskId) throws NullPointerException;

    Task getEpicTask(Integer taskId) throws NullPointerException;

    Task getSubTask(Integer taskId);

    List<Task> getAllTypesTasks();

    TreeSet<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<Task> getAllEpicTasks();

    //получить все подзадачи 1 эпика
    List<Task> getOneEpicSubTasks(Integer epicTaskId);

    List<Task> getAllSubTasks();

    boolean deleteTaskObjectById(Integer taskId);

    boolean deleteTask(Integer taskId);

    boolean deleteEpicTask(Integer taskId);

    boolean deleteSubTask(Integer taskId);

    boolean deleteOneEpicSubTasks(Integer epicTaskId) throws NoSuchElementException, NullPointerException;

    boolean deleteAllTasks();

    boolean deleteAllEpicTasks();

    boolean deleteAllSubTasks();

}