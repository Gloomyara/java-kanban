package ru.mikhailantonov.taskmanager.manager.tasks;

import com.google.gson.*;
import ru.mikhailantonov.taskmanager.server.client.KVTaskClient;
import ru.mikhailantonov.taskmanager.server.exceptions.HttpClientException;
import ru.mikhailantonov.taskmanager.server.handlers.LocalDateTimeTypeAdapter;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.exceptions.TimeStampsCrossingException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static ru.mikhailantonov.taskmanager.server.enums.KVKeys.*;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private final Gson gson;

    public HttpTaskManager(String serverUrl) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
        client = new KVTaskClient(serverUrl);
    }

    public HttpTaskManager(String serverUrl, String apiToken) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
        client = new KVTaskClient(serverUrl, apiToken);
    }

    public void loadFromServer()
            throws JsonSyntaxException, TimeStampsCrossingException, NoSuchElementException {

        try {
            JsonElement jsonElementTasks = JsonParser.parseString(client.load(TASKS.getName()));
            if (jsonElementTasks.isJsonArray()) {
                Task[] tasksArray = gson.fromJson(jsonElementTasks.getAsJsonArray(), Task[].class);
                for (Task task : tasksArray) {
                    manageTask(task);
                }
            } else {
                throw new JsonSyntaxException("Ошибка при загрузке задач с сервера");
            }
        } catch (HttpClientException | JsonSyntaxException | TimeStampsCrossingException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        try {
            JsonElement jsonElementEpics = JsonParser.parseString(client.load(EPICS.getName()));
            if (jsonElementEpics.isJsonArray()) {
                EpicTask[] tasksArray = gson.fromJson(jsonElementEpics.getAsJsonArray(), EpicTask[].class);
                for (EpicTask task : tasksArray) {
                    manageEpicTask(task);
                }
            } else {
                throw new JsonSyntaxException("Ошибка при загрузке эпиков с сервера");
            }
        } catch (HttpClientException | JsonSyntaxException | TimeStampsCrossingException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        try {
            JsonElement jsonElementSubTasks = JsonParser.parseString(client.load(SUBTASKS.getName()));
            if (jsonElementSubTasks.isJsonArray()) {
                SubTask[] tasksArray = gson.fromJson(jsonElementSubTasks.getAsJsonArray(), SubTask[].class);
                for (SubTask task : tasksArray) {
                    manageSubTask(task);
                }
            } else {
                throw new JsonSyntaxException("Ошибка при загрузке подзадач с сервера");
            }
        } catch (HttpClientException | JsonSyntaxException | TimeStampsCrossingException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        try {
            JsonElement jsonElementHistory = JsonParser.parseString(client.load(HISTORY.getName()));
            if (jsonElementHistory.isJsonArray()) {
                Task[] historyArray = gson.fromJson(jsonElementHistory.getAsJsonArray(), Task[].class);
                for (Task task : historyArray) {
                    getTaskObjectById(task.getTaskId());
                }
            } else {
                throw new JsonSyntaxException("Ошибка при загрузке истории с сервера");
            }
        } catch (HttpClientException | JsonSyntaxException | TimeStampsCrossingException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void save() {
        try {
            client.put(TASKS.getName(), gson.toJson(getAllTasks().toArray()));
        } catch (HttpClientException | JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
        try {
            client.put(EPICS.getName(), gson.toJson(getAllEpicTasks().toArray()));
        } catch (HttpClientException | JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
        try {
            client.put(SUBTASKS.getName(), gson.toJson(getAllSubTasks().toArray()));
        } catch (HttpClientException | JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
        try {
            client.put(HISTORY.getName(), gson.toJson(getHistory().toArray()));
        } catch (HttpClientException | JsonSyntaxException e) {
            System.out.println(e.getMessage());
        }
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
    public String getAPI_TOKEN() {
        return client.getAPI_TOKEN();
    }
}