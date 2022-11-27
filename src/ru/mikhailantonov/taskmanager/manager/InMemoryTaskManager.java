package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.StatusType;

import java.util.Calendar;
import java.util.HashMap;

/** Класс нашего самого 1ого менеджера, для обработки и хранения объектов задач */

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = new InMemoryHistoryManager();
    int id = 1; //было нужно для тестов
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private HashMap<Integer, Integer> epicSubTaskIdMap = new HashMap<>();

    @Override
    public String getHistory() {
        return historyManager.getHistory();
    }

    //обработка входящей задачи
    @Override
    public void manageObject(Task object) {

        if (object.getTaskId() == null) {
            object.setTaskId(id);
        }

        //условие для создания эпика
        if (object instanceof EpicTask) {
            manageEpicTask((EpicTask) object);
            //условие для создания подзадачи
        } else if (object instanceof SubTask) {
            manageSubTask((SubTask) object);
            //условие для создания задачи
        } else {
            manageTask(object);
        }
    }

    @Override
    public void manageEpicTask(EpicTask epicObject) {

        int taskId = epicObject.getTaskId();

        if (!epicTaskMap.containsKey(taskId)) {

            epicObject.setTaskStatus(epicObject.epicStatusType());
            epicObject.setTaskCreateDate(Calendar.getInstance());
            epicObject.setTaskUpdateDate(Calendar.getInstance());
            epicTaskMap.put(taskId, epicObject);
            id = id + 1;
        } else {

            EpicTask object = epicTaskMap.get(taskId);
            object.setTaskName(epicObject.getTaskName());
            object.setTaskDescription(epicObject.getTaskDescription());
            object.setTaskStatus(epicObject.epicStatusType());

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

                subObject.setTaskStatus(StatusType.NEW);
                subObject.setTaskCreateDate(Calendar.getInstance());
                subObject.setTaskUpdateDate(Calendar.getInstance());
                epicObject.getSubTaskMap().put(taskId, subObject);
                epicSubTaskIdMap.put(taskId, epicTaskId);
                id = id + 1;
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

            taskObject.setTaskStatus(StatusType.NEW);
            taskObject.setTaskCreateDate(Calendar.getInstance());
            taskObject.setTaskUpdateDate(Calendar.getInstance());
            taskMap.put(taskId, taskObject);
            id = id + 1;
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

    //метод для печати всех подзадач 1 эпика
    @Override
    public void printOneEpicSubTasks(int epicTaskId) {
        if (epicTaskMap.containsKey(epicTaskId)) {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);
            System.out.println("Подзадачи эпика: " + epicObject.getTaskName());
            for (SubTask subObject : epicObject.getSubTaskMap().values()) {
                System.out.println("Подзадача: " + subObject.getTaskName());
            }
        } else {
            System.out.println("Ошибка! эпик задача не найдена");
        }
    }

    //получить задачу по ID
    @Override
    public Task getObjectById(int taskId) {

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

    //удалить по ID
    public void deleteTaskById(int taskId) {

        if (taskMap.containsKey(taskId)) {
            deleteTask(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            deleteEpicTask(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            deleteSubTask(taskId);
        } else {
            System.out.println("Задачи с таким ID нет");
        }
    }

    @Override
    public void deleteTask(int taskId) {
        taskMap.remove(taskId);
        System.out.println("Задача под номером: " + taskId + " удалена.");
    }

    @Override
    public void deleteEpicTask(int taskId) {
        epicTaskMap.remove(taskId);
        System.out.println("Эпик под номером: " + taskId + " и все его подзадачи удалены.");
    }

    @Override
    public void deleteSubTask(int taskId) {
        int epicTaskId = epicSubTaskIdMap.get(taskId);
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        epicObject.getSubTaskMap().remove(taskId);
        epicSubTaskIdMap.remove(taskId);
        System.out.println("Подзадача эпика: " + epicTaskId + ", под номером: " + taskId + " удалена.");
    }

    //печать всех задач
    @Override
    public void printAllTypesTasks() {
        System.out.println(taskMap);
        System.out.println(epicTaskMap);
    }

    //печать задач
    @Override
    public void printAllTasks() {

        if (!taskMap.isEmpty()) {
            for (Task object : taskMap.values()) {
                System.out.println(object.getTaskName());
            }
        } else {
            System.out.println("Ошибка! не найдено задач!");
        }
    }

    //печать эпиков
    @Override
    public void printAllEpicTasks() {

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                System.out.println(epicObject.getTaskName());
            }
        } else {
            System.out.println("Ошибка! не найдено эпиков'!");
        }
    }

    //печать подзадач
    @Override
    public void printAllSubTasks() {

        if (!epicTaskMap.isEmpty()) {
            for (EpicTask epicObject : epicTaskMap.values()) {
                System.out.println("Подзадачи эпика: " + epicObject.getTaskName());
                if (!epicObject.getSubTaskMap().isEmpty()) {
                    for (SubTask subObject : epicObject.getSubTaskMap().values())
                        System.out.println("Подзадача: " + subObject.getTaskName());
                } else {
                    System.out.println("Нет подзадач");
                }
            }
        } else {
            System.out.println("Нет подзадач");
        }
    }

    //удалить все задачи
    @Override
    public void deleteAllTasks() {

        if (!taskMap.isEmpty()) {
            taskMap.clear();
            System.out.println("все задачи удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    //удалить все эпики
    @Override
    public void deleteAllEpicTasks() {

        if (!epicTaskMap.isEmpty()) {
            epicTaskMap.clear();
            System.out.println("все эпики удалены");
        } else {
            System.out.println("Нечего удалять");
        }
    }

    //удалить все подзадачи
    @Override
    public void deleteAllSubTasks() {

        if (!epicSubTaskIdMap.isEmpty()) {
            epicSubTaskIdMap.clear();
            for (EpicTask epicObject : epicTaskMap.values()) {
                if (!epicObject.getSubTaskMap().isEmpty()) {
                    epicObject.getSubTaskMap().clear();
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " все подзадачи удалены");
                } else {
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " Нечего удалять");
                }
            }
        } else {
            System.out.println("Нечего удалять");
        }
    }
}