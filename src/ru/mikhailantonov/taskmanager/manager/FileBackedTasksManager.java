package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Класс для обработки и хранения объектов задач в файле и/или создания задач из файла
 */

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    //считываем таски
    public FileBackedTasksManager(Path file) {
        load(file.toString());
    }

    //чтение файла задач и истории
    public void load(String filePath) {
        List<String> list = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                list.add(line);
            }
            int i = 1;
            while (!list.get(i).isBlank()) {
                manageTaskObject(taskFromString(list.get(i)));
                i++;
            }
            i++;
            for (int taskId : historyFromString(list.get(i))) {
                getTaskObjectById(taskId);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка во время чтения файла.");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //Сохранить задачи и историю просмотров в файл
    private void save() {
        var tasks = getAllTypesTasks();
        TaskIdComparator taskIdComparator = new TaskIdComparator();
        tasks.sort(taskIdComparator);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("resources/autosave.csv", StandardCharsets.UTF_8))) {

            bufferedWriter.write("id,type,name,status,description,epicId");
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
    public Task taskFromString(String value) throws NumberFormatException {
        String[] line = value.split(",");
        //id,type,name,status,description,epic
        switch (TaskType.valueOf(line[1])) {
            case TASK: {
                Task task = new Task(line[2], StatusType.valueOf(line[3]), line[4]);
                task.setTaskId(Integer.parseInt(line[0]));
                return task;
            }
            case EPIC: {
                Task task = new EpicTask(line[2], line[4]);
                task.setTaskId(Integer.parseInt(line[0]));
                task.setTaskStatus(StatusType.valueOf(line[3]));
                return task;
            }
            case SUBTASK: {
                Task task = new SubTask(line[2], StatusType.valueOf(line[3]), line[4], Integer.parseInt(line[5]));
                task.setTaskId(Integer.parseInt(line[0]));
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
    public Task getTaskObjectById(int taskId) {
        Task task = super.getTaskObjectById(taskId);
        save();
        return task;
    }

    @Override
    public void manageTaskObject(Task object) {
        super.manageTaskObject(object);
        save();
    }

    @Override
    public boolean deleteTaskById(int taskId) {
        boolean b = super.deleteTaskById(taskId);
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
