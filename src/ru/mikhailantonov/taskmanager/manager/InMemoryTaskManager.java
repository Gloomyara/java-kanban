package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static ru.mikhailantonov.taskmanager.util.TimeMapManager.*;

/**
 * Класс нашего самого 1ого менеджера, для обработки и хранения объектов задач
 */

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<LocalDateTime, Boolean> timeMap;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int id = 1; //нужно для тестов
    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    protected final HashMap<Integer, Integer> epicSubTaskIdMap = new HashMap<>();
    protected ArrayList<Task> tasksList;
    protected Function<Task, Integer> statusDoneLast = t1 -> (t1.getTaskStatus() != StatusType.DONE) ? 1
            : (t1.getTaskStatus() == StatusType.DONE) ? -1 : 0;
    protected Comparator<Task> comparator =
            Comparator.comparing(statusDoneLast).thenComparing(
                    Task::getStartTime, Comparator.nullsLast(Comparator.reverseOrder()));



    protected TreeSet<Task> allTasksPrioritizedSet = new TreeSet<>(comparator);

    public InMemoryTaskManager() {
        timeMap = TimeMapManager.createTimeMap(LocalDateTime.of(2023, 1, 1, 0, 0)
                , 1);
    }

    //вернуть историю просмотров
    @Override
    public List<Task> getHistory() throws NullPointerException {
        return historyManager.getHistory();
    }

    //обработка входящей задачи
    @Override
    public void manageTaskObject(Task object) {
        try {
            if (object == null) {
                throw new NullPointerException("Ошибка при обработке задачи! Невозможно обработать пустой объект задачи");
            } else {
                //присвоить id
                if (object.getTaskId() == null) {
                    //подкрутка id
                    while (taskMap.containsKey(id) || epicTaskMap.containsKey(id)
                            || epicSubTaskIdMap.containsKey(id)) {
                        id++;
                    }
                    object.setTaskId(id);
                    id = id + 1;
                }
                switch (object.getTaskType()) {
                    case TASK: {
                        manageTask(object);
                        return;
                    }
                    case SUBTASK: {
                        manageSubTask((SubTask) object);
                        return;
                    }
                    case EPIC: {
                        manageEpicTask((EpicTask) object);
                        return;
                    }
                    default:
                        throw new IllegalArgumentException("Ошибка при обработке задачи");
                }
            }
        } catch (NullPointerException | IllegalArgumentException | TimeStampsCrossingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void manageEpicTask(EpicTask epicObject)
            throws NoSuchElementException, NullPointerException, TimeStampsCrossingException {
        if (epicObject == null) {
            throw new NullPointerException("Ошибка при обработке задачи! Невозможно обработать пустой объект задачи");
        }
        Integer taskId = epicObject.getTaskId();

        if (!epicTaskMap.containsKey(taskId)) {

            epicObject.setTaskStatus(epicObject.epicStatusType());
            epicTaskMap.put(taskId, epicObject);
            allTasksPrioritizedSet.add(epicObject);
        } else {

            EpicTask epicTask = epicTaskMap.get(taskId);
            if (!epicTask.equals(epicObject)) {
                if (!epicTask.getSubTaskMap().equals(epicObject.getSubTaskMap())) {
                    deleteOneEpicSubTasks(taskId);
                    if (!epicObject.getSubTaskMap().isEmpty()) {
                        for (SubTask subTask : epicObject.getSubTaskMap().values()) {
                            manageSubTask(subTask);
                        }
                    }
                }
                epicTask.setTaskName(epicObject.getTaskName());
                epicTask.setTaskDescription(epicObject.getTaskDescription());
            }
            epicTask.setTaskStatus(epicTask.epicStatusType());
            epicTask.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public void manageSubTask(SubTask subObject)
            throws NoSuchElementException, NullPointerException, TimeStampsCrossingException {

        if (subObject == null) {
            throw new NullPointerException("Ошибка при обработке задачи! Невозможно обработать пустой объект задачи");
        }
        if (subObject.getEpicTaskId() == null) {
            throw new NullPointerException("Ошибка при обработке Подзадачи! EpicTaskId = null");
        }
        if (subObject.getTaskStatus() == null) {
            subObject.setTaskStatus(StatusType.NEW);
        }
        Integer taskId = subObject.getTaskId();
        Integer epicTaskId = subObject.getEpicTaskId();
        if (epicTaskId == null || !epicTaskMap.containsKey(epicTaskId)) {
            throw new NoSuchElementException("Ошибка! эпик задачи с таким ID нет.");
        } else {
            EpicTask epicTask = epicTaskMap.get(epicTaskId);
            if (epicTask.getSubTaskMap().containsKey(taskId)) {
                SubTask subTask = epicTask.getSubTaskMap().get(taskId);
                if (!subTask.equals(subObject)) {
                    //проверка/обновление временных меток
                    if (!manageTimeStampsMap(timeMap, subObject, subTask)) return;
                    //обновление старой задачи
                    if (subObject.getStartTime() == null) {
                        subTask.setStartTime(null);
                    } else {
                        subTask.setStartTime(subObject.getStartTime());
                        epicTask.setEpicStartTime();
                    }
                    subTask.setTaskName(subObject.getTaskName());
                    subTask.setTaskDescription(subObject.getTaskDescription());
                    subTask.setTaskStatus(subObject.getTaskStatus());
                }
                subTask.setUpdateTime(LocalDateTime.now());
                //добавление нового объекта
            } else {
                if (!startTimeAndDurationMatters.test(subObject)) {
                    //добавление меток или исключение при пересечении
                    timeMap.putAll(timeMapAddTimeStamps(timeMap, subObject));
                }
                if (subObject.getStartTime() != null) epicTask.setEpicStartTime();
                epicTask.getSubTaskMap().put(taskId, subObject);
                allTasksPrioritizedSet.add(subObject);
                epicSubTaskIdMap.put(taskId, epicTaskId);
            }
            epicTask.setEpicDuration();
            epicTask.setTaskStatus(epicTask.epicStatusType());
            epicTask.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public void manageTask(Task taskObject) throws TimeStampsCrossingException, NullPointerException {

        if (taskObject == null) {
            throw new NullPointerException("Ошибка при обработке задачи! Невозможно обработать пустой объект задачи");
        }

        if (taskObject.getTaskStatus() == null) {
            taskObject.setTaskStatus(StatusType.NEW);
        }
        Integer taskId = taskObject.getTaskId();
        //обновление
        if (taskMap.containsKey(taskId)) {

            Task task = taskMap.get(taskId);
            if (!task.equals(taskObject)) {
                //проверка/обновление временных меток
                if (!manageTimeStampsMap(timeMap, taskObject, task)) return;
                //обновление старой задачи
                if (taskObject.getStartTime() == null) {
                    task.setStartTime(null);
                } else {
                    task.setStartTime(taskObject.getStartTime());
                }
                task.setDuration(taskObject.getDuration());
                task.setTaskName(taskObject.getTaskName());
                task.setTaskDescription(taskObject.getTaskDescription());
                task.setTaskStatus(taskObject.getTaskStatus());
            }
            task.setUpdateTime(LocalDateTime.now());
            //добавление нового объекта
        } else {
            //если есть startTime и duration не равно 0;
            if (!startTimeAndDurationMatters.test(taskObject)) {
                //добавление меток или исключение при пересечении
                timeMap.putAll(timeMapAddTimeStamps(timeMap, taskObject));
            }
            taskMap.put(taskId, taskObject);
            allTasksPrioritizedSet.add(taskObject);
        }
    }


    //получить задачу по ID
    @Override
    public Task getTaskObjectById(Integer taskId) throws NoSuchElementException, NullPointerException {

        if (taskId == null) {
            throw new NullPointerException("Ошибка при обработке taskId! taskId = null");
        }
        if (taskMap.containsKey(taskId)) {
            return getTask(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            return getEpicTask(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            return getSubTask(taskId);
        }
        throw new NoSuchElementException("Ошибка! Задача с ID:" + taskId + " не найдена");
    }

    @Override
    public Task getTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!taskMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Задача с ID:" + taskId + " не найдена");
        }
        Task task = taskMap.get(taskId);
        task.setUpdateTime(LocalDateTime.now());
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getEpicTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!epicTaskMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Эпик с ID: " + taskId + " не найден");
        }
        Task task = epicTaskMap.get(taskId);
        task.setUpdateTime(LocalDateTime.now());
        historyManager.add(task);
        return task;
    }

    @Override
    public Task getSubTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!epicSubTaskIdMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Подзадача с ID: " + taskId + " не найдена");
        }
        int epicTaskId = epicSubTaskIdMap.get(taskId);
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        if (!epicObject.getSubTaskMap().containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! В эпике: " + epicTaskId + " подзадача с ID: " + taskId + " не найдена");
        }
        Task task = epicObject.getSubTaskMap().get(taskId);
        task.setUpdateTime(LocalDateTime.now());
        epicObject.setUpdateTime(LocalDateTime.now());
        historyManager.add(task);
        return task;
    }

    //получить все задачи всех типов
    @Override
    public List<Task> getAllTypesTasks() {
        ArrayList<Task> allTasksList = new ArrayList<>();
        if (!getAllTasks().isEmpty()) {
            allTasksList.addAll(getAllTasks());
        }
        if (!getAllEpicTasks().isEmpty()) {
            allTasksList.addAll(getAllEpicTasks());
        }
        if (!getAllSubTasks().isEmpty()) {
            allTasksList.addAll(getAllSubTasks());
        }
        return allTasksList;
    }

    //получить отсортированный список задач
    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return allTasksPrioritizedSet;
    }

    //получить все задачи
    @Override
    public List<Task> getAllTasks() {
        tasksList = new ArrayList<>();

        if (!taskMap.isEmpty()) {
            tasksList.addAll(taskMap.values());
        }
        return tasksList;
    }

    //получить все эпики
    @Override
    public List<Task> getAllEpicTasks() {
        tasksList = new ArrayList<>();

        if (!epicTaskMap.isEmpty()) {
            tasksList.addAll(epicTaskMap.values());
        }
        return tasksList;
    }

    //получить все подзадачи 1 эпика
    @Override
    public List<Task> getOneEpicSubTasks(Integer epicTaskId) throws NoSuchElementException, NullPointerException {
        if (epicTaskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        tasksList = new ArrayList<>();

        if (epicTaskMap.containsKey(epicTaskId)) {
            EpicTask epicObject = epicTaskMap.get(epicTaskId);
            historyManager.add(epicObject);
            if (epicObject.getSubTaskMap().isEmpty()){
                return tasksList;
            }
            tasksList.addAll(epicObject.getSubTaskMap().values());
            for (Task subTask : tasksList) {
                historyManager.add(subTask);
            }
        } else {
            throw new NoSuchElementException("Ошибка! Эпик с ID:" + epicTaskId + " не найден");
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
        }
        return tasksList;
    }

    //удалить по ID
    @Override
    public boolean deleteTaskObjectById(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (taskMap.containsKey(taskId)) {
            return deleteTask(taskId);
        } else if (epicTaskMap.containsKey(taskId)) {
            return deleteEpicTask(taskId);
        } else if (epicSubTaskIdMap.containsKey(taskId)) {
            return deleteSubTask(taskId);
        } else {
            throw new NoSuchElementException("Ошибка! Задача с ID:" + taskId + " не найдена");
        }
    }

    @Override
    public boolean deleteTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!taskMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Задача с ID:" + taskId + " не найдена");
        }
        Task task = taskMap.get(taskId);
        allTasksPrioritizedSet.remove(task);
        timeMap.putAll(timeMapRemoveTimeStamps(task));
        taskMap.remove(taskId);
        historyManager.remove(taskId);
        System.out.println("Задача под номером: " + taskId + " удалена.");
        return true;
    }

    @Override
    public boolean deleteEpicTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!epicTaskMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Эпик с ID:" + taskId + " не найден");
        }
        EpicTask epicObject = epicTaskMap.get(taskId);
        for (Task subTask : epicObject.getSubTaskMap().values()) {
            historyManager.remove(subTask.getTaskId());
            epicSubTaskIdMap.remove(subTask.getTaskId());
            allTasksPrioritizedSet.remove(subTask);
            timeMap.putAll(timeMapRemoveTimeStamps(subTask));
        }
        allTasksPrioritizedSet.remove(epicObject);
        historyManager.remove(taskId);
        epicTaskMap.remove(taskId);
        System.out.println("Эпик под номером: " + taskId + " и все его подзадачи удалены.");
        return true;
    }

    @Override
    public boolean deleteSubTask(Integer taskId) throws NoSuchElementException, NullPointerException {
        if (taskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!epicSubTaskIdMap.containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! Подзадача с ID: " + taskId + " не найдена");
        }
        int epicTaskId = epicSubTaskIdMap.get(taskId);
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        if (!epicObject.getSubTaskMap().containsKey(taskId)) {
            throw new NoSuchElementException("Ошибка! В эпике: " + epicTaskId + " подзадача с ID: " + taskId + " не найдена");
        }
        Task subTask = epicObject.getSubTaskMap().get(taskId);
        timeMap.putAll(timeMapRemoveTimeStamps(subTask));
        epicObject.getSubTaskMap().remove(taskId);
        epicObject.setEpicDuration();
        epicObject.setEpicStartTime();
        allTasksPrioritizedSet.remove(subTask);
        epicSubTaskIdMap.remove(taskId);
        historyManager.remove(taskId);
        System.out.println("Подзадача эпика: " + epicTaskId + ", под номером: " + taskId + " удалена.");
        return true;
    }

    @Override
    public boolean deleteOneEpicSubTasks(Integer epicTaskId) throws NoSuchElementException, NullPointerException {
        if (epicTaskId == null) {
            throw new NullPointerException("Ошибка! taskId = null");
        }
        if (!epicTaskMap.containsKey(epicTaskId)) {
            throw new NoSuchElementException("Ошибка! Эпик с ID:" + epicTaskId + " не найден");
        }
        EpicTask epicObject = epicTaskMap.get(epicTaskId);
        if (epicObject.getSubTaskMap().isEmpty()) {
            System.out.println("В эпике с ID: " + epicTaskId + " нет подзадач");
            return false;
        }
        for (Task subTask : epicObject.getSubTaskMap().values()) {
            historyManager.remove(subTask.getTaskId());
            epicSubTaskIdMap.remove(subTask.getTaskId());
            allTasksPrioritizedSet.remove(subTask);
            timeMap.putAll(timeMapRemoveTimeStamps(subTask));
        }
        System.out.println("В эпике с ID: " + epicTaskId + " все подзадачи удалены.");
        return true;
    }

    //удалить все задачи
    @Override
    public boolean deleteAllTasks() {

        if (!taskMap.isEmpty()) {
            for (Task task : taskMap.values()) {
                historyManager.remove(task.getTaskId());
                allTasksPrioritizedSet.remove(task);
                timeMap.putAll(timeMapRemoveTimeStamps(task));
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
                if (id > epicObject.getTaskId()) id = epicObject.getTaskId();
                for (Task subTask : epicObject.getSubTaskMap().values()) {
                    if (!epicObject.getSubTaskMap().isEmpty()) {
                        historyManager.remove(subTask.getTaskId());
                        allTasksPrioritizedSet.remove(subTask);
                        timeMap.putAll(timeMapRemoveTimeStamps(subTask));
                    }
                }
                historyManager.remove(epicObject.getTaskId());
                allTasksPrioritizedSet.remove(epicObject);
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
                    for (Task subTask : epicObject.getSubTaskMap().values()) {
                        timeMap.putAll(timeMapRemoveTimeStamps(subTask));
                        historyManager.remove(subTask.getTaskId());
                        allTasksPrioritizedSet.remove(subTask);
                    }
                    epicObject.getSubTaskMap().clear();
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " все подзадачи удалены");
                } else {
                    System.out.println("В эпике под номером: " + epicObject.getTaskId() + " Нечего удалять");
                }
                epicObject.setEpicDuration();
                epicObject.setEpicStartTime();
            }
            return true;
        } else {
            System.out.println("Нечего удалять");
            return false;
        }
    }
}