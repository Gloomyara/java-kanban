package ru.mikhailantonov.taskmanager.manager.tasks;

import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Базовый интерфейс менеджеров по работе с задачами
 */

public interface TaskManager {

    List<Task> getHistory();

    void manageTaskObject(Task object);

    void manageTask(Task taskObject);

    void manageSubTask(SubTask subObject);

    void manageEpicTask(EpicTask epicObject);

    //получить задачу по ID
    Task getTaskObjectById(Integer taskId);

    Task getTask(Integer taskId);

    Task getEpicTask(Integer taskId);

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

    boolean deleteOneEpicSubTasks(Integer epicTaskId);

    boolean deleteAllTasks();

    boolean deleteAllEpicTasks();

    boolean deleteAllSubTasks();

    HashSet<LocalDateTime> getTimeStampsSet();
}