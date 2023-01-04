package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.Managers;
import ru.mikhailantonov.taskmanager.util.StatusType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Класс нашего самого 1ого менеджера, для обработки и хранения объектов задач
 */

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    int id = 1; //нужно для тестов
    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    protected final HashMap<Integer, Integer> epicSubTaskIdMap = new HashMap<>();
    protected ArrayList<Task> tasksList;

    //вернуть историю просмотров
    @Override
    public List<Task> getHistory() {
        if (historyManager.getHistory() != null) {
            return historyManager.getHistory();
        } else {
            System.out.println("Ошибка! История просмотров не найдена");
            return null;
        }
    }

    //обработка входящей задачи
    @Override
    public void manageTaskObject(Task object) {
        //подкрутка id
        while (taskMap.containsKey(id) && epicTaskMap.containsKey(id) && epicSubTaskIdMap.containsKey(id)) {
            id++;
        }
        //присвоить id
        if (object.getTaskId() == null) {
            object.setTaskId(id);
            id = id + 1;
        }
        //дата
        if (object.getTaskCreateDate() == null) {
            object.setTaskCreateDate(Calendar.getInstance());
            object.setTaskUpdateDate(Calendar.getInstance());
        }
        //условие для создания эпика
        if (object instanceof EpicTask) {
            manageEpicTask((EpicTask) object);
        //условие для создания подзадачи
        } else if (object instanceof SubTask) {
            if (object.getTaskStatus() == null) {
                object.setTaskStatus(StatusType.NEW);
            }
            manageSubTask((SubTask) object);
        //условие для создания задачи
        } else {
            if (object.getTaskStatus() == null) {
                object.setTaskStatus(StatusType.NEW);
            }
            manageTask(object);
        }
    }

    @Override
    public void manageEpicTask(EpicTask epicObject) {

        int taskId = epicObject.getTaskId();

        if (!epicTaskMap.containsKey(taskId)) {

            epicObject.setTaskStatus(epicObject.epicStatusType());
            epicTaskMap.put(taskId, epicObject);

        } else {

            EpicTask object = epicTaskMap.get(taskId);
            object.setTaskName(epicObject.getTaskName());
            object.setTaskDescription(epicObject.getTaskDescription());
            object.setTaskStatus(object.epicStatusType());

            if (object.getTaskStatus() == StatusType.DONE) {
                object.setCloseDate(Calendar.getInstance());
            } else {
                object.setTaskUpdateDate(Calendar.getInstance());
            }
        }
    }

    @Override
    public void manageSubTask(SubTask subObject) {

        int taskId = subObject.getTaskId();
        int epicTaskId = subObject.getEpicTaskId();
        if (!epicTaskMap.containsKey(epicTaskId)) {
            System.out.println("Ошибка! эпик задачи с таким ID нет.");
        } else {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);

            if (!epicObject.getSubTaskMap().containsKey(taskId)) {

                epicObject.getSubTaskMap().put(taskId, subObject);
                epicSubTaskIdMap.put(taskId, epicTaskId);
                epicObject.setTaskStatus(epicObject.epicStatusType());

            } else {

                SubTask object = epicObject.getSubTaskMap().get(taskId);
                object.setTaskName(subObject.getTaskName());
                object.setTaskDescription(subObject.getTaskDescription());
                object.setTaskStatus(subObject.getTaskStatus());

                if (object.getTaskStatus() == StatusType.DONE) {
                    object.setCloseDate(Calendar.getInstance());
                } else {
                    object.setTaskUpdateDate(Calendar.getInstance());
                }
                epicObject.setTaskStatus(epicObject.epicStatusType());
            }
        }
    }

    @Override
    public void manageTask(Task taskObject) {

        int taskId = taskObject.getTaskId();

        if (!taskMap.containsKey(taskId)) {

            taskMap.put(taskId, taskObject);

        } else {

            Task object = taskMap.get(taskId);
            object.setTaskName(taskObject.getTaskName());
            object.setTaskDescription(taskObject.getTaskDescription());
            object.setTaskStatus(taskObject.getTaskStatus());

            if (object.getTaskStatus() == StatusType.DONE) {
                object.setCloseDate(Calendar.getInstance());
            } else {
                object.setTaskUpdateDate(Calendar.getInstance());
            }
        }
    }

    //получить задачу по ID
    @Override
    public Task getTaskObjectById(int taskId) {

        if (taskMap.containsKey(taskId)) {
            return getTask(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            return getEpicTask(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            return getSubTask(taskId);
        }
        return null;
    }

    @Override
    public Task getTask(int taskId) {
        Task task = taskMap.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getEpicTask(int taskId) {
        Task task = epicTaskMap.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getSubTask(int taskId) {
        int epicTaskId = epicSubTaskIdMap.get(taskId);
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        Task task = epicObject.getSubTaskMap().get(taskId);
        historyManager.add(task);
        return task;
    }

    //получить все задачи всех типов
    @Override
    public List<Task> getAllTypesTasks() {
        ArrayList<Task> allTasksList = new ArrayList<>();
        if (getAllTasks() != null) {
            allTasksList.addAll(getAllTasks());
        }
        if (getAllEpicTasks() != null) {
            allTasksList.addAll(getAllEpicTasks());
        }
        if (getAllSubTasks() != null) {
            allTasksList.addAll(getAllSubTasks());
        }

        return allTasksList;
    }

    //получить все задачи
    @Override
    public List<Task> getAllTasks() {
        tasksList = new ArrayList<>();

        if (!taskMap.isEmpty()) {
            tasksList.addAll(taskMap.values());
        } else {
            return null;
        }
        return tasksList;
    }

    //получить все эпики
    @Override
    public List<Task> getAllEpicTasks() {
        tasksList = new ArrayList<>();

        if (!epicTaskMap.isEmpty()) {
            tasksList.addAll(epicTaskMap.values());
        } else {
            return null;
        }
        return tasksList;
    }

    //получить все подзадачи 1 эпика
    @Override
    public List<Task> getOneEpicSubTasks(int epicTaskId) {

        tasksList = new ArrayList<>();

        if (epicTaskMap.containsKey(epicTaskId)) {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);
            tasksList.addAll(epicObject.getSubTaskMap().values());
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
            return null;
        }
        return tasksList;
    }

    //получить все подзадачи
    @Override
    public List<Task> getAllSubTasks() {
        tasksList = new ArrayList<>();

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                if (!epicObject.getSubTaskMap().isEmpty()) {
                    tasksList.addAll(epicObject.getSubTaskMap().values());
                }
            }
        } else {
            return null;
        }
        return tasksList;
    }

    //удалить по ID
    public boolean deleteTaskById(int taskId) {

        if (taskMap.containsKey(taskId)) {
            return deleteTask(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            return deleteEpicTask(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            return deleteSubTask(taskId);
        } else {
            System.out.println("Задачи с таким ID нет");
            return false;
        }
    }

    @Override
    public boolean deleteTask(int taskId) {
        taskMap.remove(taskId);
        historyManager.remove(taskId);
        System.out.println("Задача под номером: " + taskId + " удалена.");
        return true;
    }

    @Override
    public boolean deleteEpicTask(int taskId) {
        EpicTask epicObject = epicTaskMap.get(taskId);
        for (Task task : epicObject.getSubTaskMap().values()) {
            historyManager.remove(task.getTaskId());
            epicSubTaskIdMap.remove(task.getTaskId());
        }
        historyManager.remove(taskId);
        epicTaskMap.remove(taskId);
        System.out.println("Эпик под номером: " + taskId + " и все его подзадачи удалены.");
        return true;
    }

    @Override
    public boolean deleteSubTask(int taskId) {
        int epicTaskId = epicSubTaskIdMap.get(taskId);
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        epicObject.getSubTaskMap().remove(taskId);
        epicSubTaskIdMap.remove(taskId);
        historyManager.remove(taskId);
        System.out.println("Подзадача эпика: " + epicTaskId + ", под номером: " + taskId + " удалена.");
        return true;
    }

    //удалить все задачи
    @Override
    public boolean deleteAllTasks() {

        if (!taskMap.isEmpty()) {
            for (Task task : taskMap.values()) {
                historyManager.remove(task.getTaskId());
            }
            taskMap.clear();
            System.out.println("все задачи удалены");
            return true;
        } else {
            System.out.println("Нечего удалять");
            return false;
        }
    }

    //удалить все эпики
    @Override
    public boolean deleteAllEpicTasks() {

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                for (Task task : epicObject.getSubTaskMap().values()) {
                    if (!epicObject.getSubTaskMap().isEmpty()) {
                        historyManager.remove(task.getTaskId());
                    }
                }
                historyManager.remove(epicObject.getTaskId());
            }
            epicSubTaskIdMap.clear();
            epicTaskMap.clear();
            System.out.println("все эпики удалены");
            return true;
        } else {
            System.out.println("Нечего удалять");
            return false;
        }
    }

    //удалить все подзадачи
    @Override
    public boolean deleteAllSubTasks() {

        if (!epicSubTaskIdMap.isEmpty()) {
            epicSubTaskIdMap.clear();
            for (EpicTask epicObject : epicTaskMap.values()) {
                if (!epicObject.getSubTaskMap().isEmpty()) {
                    for (Task task : epicObject.getSubTaskMap().values()) {
                        historyManager.remove(task.getTaskId());
                    }
                    epicObject.getSubTaskMap().clear();
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " все подзадачи удалены");
                } else {
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " Нечего удалять");
                }
            }
            return true;
        } else {
            System.out.println("Нечего удалять");
            return false;
        }
    }
}