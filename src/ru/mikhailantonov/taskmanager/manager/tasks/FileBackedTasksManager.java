package ru.mikhailantonov.taskmanager.manager.tasks;

import ru.mikhailantonov.taskmanager.manager.history.HistoryManager;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.task.enums.TaskType;
import ru.mikhailantonov.taskmanager.util.FileManager;
import ru.mikhailantonov.taskmanager.util.exceptions.ManagerSaveException;
import ru.mikhailantonov.taskmanager.util.exceptions.TimeStampsCrossingException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс для обработки и хранения объектов задач в файле и/или создания задач из файла
 */

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File autoSave;

    public FileBackedTasksManager() {
    }

    //считываем таски
    public FileBackedTasksManager(File file) {
        super();
        this.autoSave = file;
    }

    //чтение файла задач и истории
    public static FileBackedTasksManager loadFromFile(File file)
            throws ManagerSaveException, TimeStampsCrossingException, NoSuchElementException {
        List<String> list = new ArrayList<>();
        FileBackedTasksManager ftm = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                list.add(line);
            }
            int i = 1;
            if (i >= list.size()) {
                throw new ManagerSaveException("Ошибка! Невозможно загрузить данные из пустого файла");
            }
            while (!list.get(i).isBlank()) {
                ftm.manageTaskObject(ftm.taskFromString(list.get(i)));
                i++;
            }
            i++;
            if (i < list.size()) {
                for (int taskId : historyFromString(list.get(i))) {
                    ftm.getTaskObjectById(taskId);
                }
            } else {
                throw new ManagerSaveException("Не удалось загрузить список истории");
            }
        } catch (IOException | NumberFormatException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.", e);
        }
        return ftm;
    }

    //Сохранить задачи и историю просмотров в файл
    protected void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(autoSave, StandardCharsets.UTF_8))) {
            var tasks = getAllTypesTasks();
            Comparator<Task> taskIdComparator = Comparator.comparingInt(Task::getTaskId);
            tasks.sort(taskIdComparator);
            bufferedWriter.write("startTime,id,type,name,status,description,duration,epicId");
            bufferedWriter.newLine();

            for (Task task : tasks) {
                bufferedWriter.write(task.toString());
                bufferedWriter.newLine();
            }

            bufferedWriter.newLine();
            bufferedWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        }
    }

    //создание задачи по данным из строки
    public Task taskFromString(String value) throws IllegalArgumentException {
        String[] line = value.split(",");
        LocalDateTime startTime;
        if (line[0].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(line[0], FileManager.DATE_TIME_FORMATTER);
        }
        switch (TaskType.valueOf(line[2])) { //startTime,id,type,name,status,description,duration,epicId
            case TASK: {
                Task task = new Task(startTime, line[3], StatusType.fromString(line[4]),
                        line[5], Integer.parseInt(line[6]));

                task.setTaskId(Integer.parseInt(line[1]));
                if (id <= task.getTaskId()) id = task.getTaskId() + 1;
                return task;
            }
            case EPIC: {
                Task task = new EpicTask(line[3], line[5]);
                task.setTaskId(Integer.parseInt(line[1]));
                if (id <= task.getTaskId()) id = task.getTaskId() + 1;
                task.setTaskStatus(StatusType.fromString(line[4]));
                return task;
            }
            case SUBTASK: {
                Task task = new SubTask(startTime, line[3], StatusType.fromString(line[4]),
                        line[5], Integer.parseInt(line[6]), Integer.parseInt(line[7]));

                task.setTaskId(Integer.parseInt(line[1]));
                if (id <= task.getTaskId()) id = task.getTaskId() + 1;
                return task;
            }
            default:
                return null;
        }
    }

    //сохранить историю в строке
    static String historyToString(HistoryManager manager) {

        StringJoiner stringJoiner = new StringJoiner(",");
        for (Task task : manager.getHistory()) {
            stringJoiner.add(task.getTaskId().toString());
        }
        return stringJoiner.toString();
    }

    //восстановить историю
    static List<Integer> historyFromString(String value) throws NumberFormatException {
        String[] line = value.split(",");
        List<Integer> tasksId = new ArrayList<>();
        for (String str : line) {
            tasksId.add(Integer.parseInt(str));
        }
        return tasksId;
    }

    @Override
    public Task getTaskObjectById(Integer taskId) {
        Task task = super.getTaskObjectById(taskId);
        save();
        return task;
    }

    @Override
    public void manageTaskObject(Task object) throws IllegalArgumentException, TimeStampsCrossingException {
        super.manageTaskObject(object);
        save();
    }

    @Override
    public boolean deleteTaskObjectById(Integer taskId) {
        boolean b = super.deleteTaskObjectById(taskId);
        save();
        return b;
    }

    @Override
    public boolean deleteAllTasks() {
        boolean b = super.deleteAllTasks();
        save();
        return b;
    }

    @Override
    public boolean deleteAllEpicTasks() {
        boolean b = super.deleteAllEpicTasks();
        save();
        return b;
    }

    @Override
    public boolean deleteAllSubTasks() {
        boolean b = super.deleteAllSubTasks();
        save();
        return b;
    }

    public File getAutoSave() {
        return autoSave;
    }
}
