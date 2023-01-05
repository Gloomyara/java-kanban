package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    //считываем таски
    public FileBackedTasksManager() {
        load("resources/autosave.csv");
    }

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
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка во время чтения файла.");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка во время чтения файла. Нет данных о истории просмотров");
        }
    }

    private void save() {
        var tasks = getAllTypesTasks();
        TaskIdComparator taskIdComparator = new TaskIdComparator();
        tasks.sort(taskIdComparator);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("resources/autosave.csv", StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic\n");
            for (Task task : tasks) {
                bufferedWriter.write(task.toString() + "\n");
            }

            bufferedWriter.newLine();
            bufferedWriter.write(historyToString(historyManager) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Произошла ошибка во время записи файла.");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }


    public Task taskFromString(String value) {
        String[] line = value.split(",");
        //id,type,name,status,description,epic
        switch (line[1]) {
            case "Задача": {
                Task task = new Task(line[2], StatusType.stringToStatus(line[3]), line[4]);
                task.setTaskId(Integer.parseInt(line[0]));
                return task;
            }
            case "Эпик": {
                Task task = new EpicTask(line[2], line[4]);
                task.setTaskId(Integer.parseInt(line[0]));
                task.setTaskStatus(StatusType.stringToStatus(line[3]));
                return task;
            }
            case "Подзадача": {
                Task task = new SubTask(line[2], StatusType.stringToStatus(line[3]), line[4], Integer.parseInt(line[5]));
                task.setTaskId(Integer.parseInt(line[0]));
                return task;
            }
        }
        return null;
    }

    static String historyToString(HistoryManager manager) {
        var tasksHistory = manager.getHistory();
        List<String> stringHistory = new ArrayList<>();
        for (Task task : tasksHistory) {
            stringHistory.add(task.getTaskId().toString());
        }
        return String.join(",", stringHistory);
    }

    static List<Integer> historyFromString(String value) throws NumberFormatException {
        String[] line = value.split(",");
        List<Integer> tasksId = new ArrayList<>();
        for (String str : line) {
            tasksId.add(Integer.parseInt(str));
        }
        return tasksId;
    }
    @Override
    public List<Task> getHistory(){
        save();
        return super.getHistory();
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
