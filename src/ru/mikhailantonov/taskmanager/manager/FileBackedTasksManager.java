package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Класс для обработки и хранения объектов задач в файле и/или создания задач из файла
 */

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File autoSave;

    //считываем таски
    public FileBackedTasksManager(File file) {
        super();
        this.autoSave = file;
    }

    //чтение файла задач и истории
    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> list = new ArrayList<>();
        FileBackedTasksManager ftm = new FileBackedTasksManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                list.add(line);
            }
            int i = 1;
            while (!list.get(i).isBlank()) {
                ftm.manageTaskObject(ftm.taskFromString(list.get(i)));
                i++;
            }
            i++;
            for (int taskId : historyFromString(list.get(i))) {
                ftm.getTaskObjectById(taskId);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка во время чтения файла.");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (TimeStampsCrossingException e) {
            System.out.println(e.getMessage());
        }
        return ftm;
    }

    //Сохранить задачи и историю просмотров в файл
    private void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(autoSave, StandardCharsets.UTF_8))) {

            bufferedWriter.write("startTime,id,type,name,status,description,duration,epicId");
            bufferedWriter.newLine();

            for (Task task : allTasksPrioritizedSet) {
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

}
