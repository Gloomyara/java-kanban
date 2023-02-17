package ru.mikhailantonov.taskmanager.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.manager.tasks.InMemoryTaskManager;
import ru.mikhailantonov.taskmanager.server.exceptions.HttpClientException;
import ru.mikhailantonov.taskmanager.server.handlers.LocalDateTimeTypeAdapter;
import ru.mikhailantonov.taskmanager.server.handlers.TaskDeserializer;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mikhailantonov.taskmanager.util.TimeStampsManager.taskTimeValidation;

class HttpTaskServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    Gson gson;
    InMemoryTaskManager taskManager;
    HttpTaskServer httpTaskServer;
    String serverUrl = "http://localhost:8081";
    Task task1;
    Task task2;
    Task task3;
    EpicTask epicTask1;
    EpicTask epicTask2;
    EpicTask epicTask3;
    SubTask subTask11;
    SubTask subTask21;
    SubTask subTask22;
    SubTask subTask31;
    SubTask subTask32;
    SubTask subTask33;

    @BeforeEach
    void createSomeTasks() {
        TaskDeserializer deserializer = new TaskDeserializer("taskType");
        deserializer.registerTaskType("TASK", Task.class);
        deserializer.registerTaskType("SUBTASK", SubTask.class);
        deserializer.registerTaskType("EPIC", EpicTask.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Task.class, deserializer)
                .create();

        task1 = new Task("Test NewTask1", "Test NewTask1 description");
        task2 = new Task("Test NewTask2", "Test NewTask2 description");
        task3 = new Task("Test NewTask3", "Test NewTask3 description");

        epicTask1 = new EpicTask("Test NewEpicTask1", "Test NewEpicTask1 description");
        subTask11 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 0),
                "Test NewSubTask11", StatusType.NEW,
                "Test NewSubTask11 description", 30, epicTask1.getTaskId());

        epicTask2 = new EpicTask("Test NewEpicTask2", "Test NewEpicTask2 description");
        subTask21 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 30),
                "Test NewSubTask21", StatusType.NEW,
                "Test NewSubTask21 description", 30, epicTask2.getTaskId());
        subTask22 = new SubTask(LocalDateTime.of(2023, 1, 1, 1, 0),
                "Test NewSubTask22", StatusType.DONE,
                "Test NewSubTask22 description", 30, epicTask2.getTaskId());

        epicTask3 = new EpicTask("Test NewEpicTask3", "Test NewEpicTask3 description");
        subTask31 = new SubTask(LocalDateTime.of(2023, 1, 1, 1, 30),
                "Test NewSubTask31", StatusType.NEW,
                "Test NewSubTask31 description", 30, epicTask3.getTaskId());
        subTask32 = new SubTask(LocalDateTime.of(2023, 1, 1, 2, 0),
                "Test NewSubTask32", StatusType.NEW,
                "Test NewSubTask32 description", 30, epicTask3.getTaskId());
        subTask33 = new SubTask(LocalDateTime.of(2023, 1, 1, 2, 30),
                "Test NewSubTask33", StatusType.NEW,
                "Test NewSubTask3 description", 30, epicTask3.getTaskId());
    }

    @BeforeEach
    void createManager() {
        taskManager = new InMemoryTaskManager();
        try {
            httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " || " + e.getCause());
        }
    }

    @AfterEach
    void stopServers() {
        httpTaskServer.stop();
    }

    @Test
    public void testPOSTNewTasks() {
        String taskJson = gson.toJson(task1);
        POST("task", taskJson);
        assertEquals(1, taskManager.getAllTasks().size(), "Задача не была добавлена");
    }

    @Test
    public void testPOSTRenewTasks() {
        task1 = new Task(1, "Test NewTask", StatusType.NEW, "Test NewTask description",
                LocalDateTime.of(2023, 1, 1, 0, 15), 30);
        task2 = new Task(1, "Test NewTask", StatusType.NEW, "Test NewTask description",
                LocalDateTime.of(2023, 1, 1, 0, 15), 45);
        final int taskId = 1;
        task1.setTaskId(1);
        task2.setTaskId(1);
        String taskJson1 = gson.toJson(task1);
        System.out.println(taskJson1);
        POST("task", taskJson1);
        String taskJson2 = gson.toJson(task2);
        System.out.println(taskJson2);
        POST("task", taskJson2);
        final Task savedTask = taskManager.getTaskObjectById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task2, savedTask, "Задачи не совпадают.");
        assertEquals(savedTask.getStartTime(), task2.getStartTime());
        assertEquals(savedTask.getDuration(), task2.getDuration());
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), task2), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testPOSTNewEpicTasks() {
        String taskJson = gson.toJson(epicTask1);
        POST("epic", taskJson);
        assertEquals(1, taskManager.getAllEpicTasks().size(), "Задача не была добавлена");
    }

    @Test
    void testPOSTNewSubTask() {
        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        subTask11.setEpicTaskId(epicTaskId);
        String taskJson = gson.toJson(subTask11);
        POST("subtask", taskJson);
        final List<Task> tasks = taskManager.getAllSubTasks();
        final List<Task> oneEpicSubTasks = taskManager.getOneEpicSubTasks(epicTaskId);
        assertFalse(tasks.isEmpty(), "Задачи нe возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, oneEpicSubTasks.size(), "Неверное количество задач.");
    }

    @Test
    void testPOSTRenewSubTask() {
        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        subTask11 = new SubTask(2, "Test oldSubTask", StatusType.NEW, "Test oldSubTask description",
                LocalDateTime.of(2023, 1, 1, 0, 15), 30, epicTaskId);
        subTask21 = new SubTask(2, "Test oldSubTask", StatusType.NEW, "Test oldSubTask description",
                LocalDateTime.of(2023, 1, 1, 0, 0), 45, epicTaskId);

        String taskJson1 = gson.toJson(subTask11);
        POST("subtask", taskJson1);
        final int taskId = subTask11.getTaskId();
        String taskJson2 = gson.toJson(subTask21);
        POST("subtask", taskJson2);
        final Task savedTask = taskManager.getTaskObjectById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask.getStartTime(), subTask21.getStartTime(), "Время старта задачи не совпадает");
        assertEquals(savedTask.getDuration(), subTask21.getDuration(), "Длительность задачи не совпадает");
        assertEquals(subTask21, savedTask, "Задачи не совпадают.");
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), subTask21), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllSubTasks();
        final List<Task> oneEpicSubTasks = taskManager.getOneEpicSubTasks(epicTaskId);
        assertFalse(tasks.isEmpty(), "Задачи нe возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, oneEpicSubTasks.size(), "Неверное количество задач.");
        assertEquals(subTask21, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void testGETTaskObjectById() {
        taskManager.manageTaskObject(task1);
        final int taskId = task1.getTaskId();
        JsonElement jsonElementTasks = JsonParser.parseString(GET("task/?id=" + taskId));
        Task task = gson.fromJson(jsonElementTasks.getAsJsonObject(), Task.class);
        assertNotNull(task, "Задача не найдена.");
        assertEquals(task1, task, "Задачи не совпадают");

        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        JsonElement jsonElementEpics = JsonParser.parseString(GET("epic/?id=" + epicTaskId));
        EpicTask epicTask = gson.fromJson(jsonElementEpics.getAsJsonObject(), EpicTask.class);
        assertNotNull(epicTask, "Задача не найдена.");
        assertEquals(epicTask1, epicTask, "Задачи не совпадают");

        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        final int subTaskTaskId = subTask11.getTaskId();
        JsonElement jsonElementSubtasks = JsonParser.parseString(GET("subtask/?id=" + subTaskTaskId));
        SubTask subTask = gson.fromJson(jsonElementSubtasks.getAsJsonObject(), SubTask.class);
        assertNotNull(subTask, "Задача не найдена.");
        assertEquals(subTask11, subTask, "Задачи не совпадают");
    }

    @Test
    void testGETTaskObjectByIdShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        HttpClientException ex1 = Assertions.assertThrows(
                HttpClientException.class,
                () -> GET("task/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: task/?id=123456789не удалась. Код ответа: 404", ex1.getMessage());

        HttpClientException ex2 = Assertions.assertThrows(
                HttpClientException.class,
                () -> GET("subtask/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: subtask/?id=123456789не удалась. Код ответа: 404", ex2.getMessage());

        HttpClientException ex3 = Assertions.assertThrows(
                HttpClientException.class,
                () -> GET("epic/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: epic/?id=123456789не удалась. Код ответа: 404", ex3.getMessage());

        HttpClientException ex4 = Assertions.assertThrows(
                HttpClientException.class,
                () -> GET("subtask/epic/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: subtask/epic/?id=123456789не удалась. Код ответа: 404", ex4.getMessage());

    }

    @Test
    void testGETOneEpicSubTasks() {
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);

        taskManager.manageTaskObject(epicTask2);
        final int epicTaskId = epicTask2.getTaskId();
        subTask21.setEpicTaskId(epicTaskId);
        subTask22.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask22);
        JsonElement jsonElementSubtasks = JsonParser.parseString(GET("subtask/epic/?id=" + epicTaskId));
        SubTask[] subTasks = gson.fromJson(jsonElementSubtasks.getAsJsonArray(), SubTask[].class);
        assertEquals(2, subTasks.length, "Неверное количество задач.");
        assertEquals(subTasks[0], subTask21, "Задачи не совпадают");
        assertEquals(subTasks[1], subTask22, "Задачи не совпадают");
    }

    @Test
    void testGETAllTasks() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(epicTask2);
        final int epicTaskId = epicTask2.getTaskId();
        subTask21.setEpicTaskId(epicTaskId);
        subTask22.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask22);
        JsonElement jsonElementSubtasks = JsonParser.parseString(GET("subtask/"));
        SubTask[] subTasks = gson.fromJson(jsonElementSubtasks.getAsJsonArray(), SubTask[].class);
        assertEquals(3, subTasks.length, "Неверное количество задач.");
        assertEquals(subTasks[0], subTask11, "Задачи не совпадают");
        assertEquals(subTasks[1], subTask21, "Задачи не совпадают");
        assertEquals(subTasks[2], subTask22, "Задачи не совпадают");
        JsonElement jsonElementTasks = JsonParser.parseString(GET("task/"));
        Task[] tasks = gson.fromJson(jsonElementTasks.getAsJsonArray(), Task[].class);
        assertEquals(1, tasks.length, "Неверное количество задач.");
        assertEquals(tasks[0], task1, "Задачи не совпадают");
        JsonElement jsonElementEpics = JsonParser.parseString(GET("epic/"));
        EpicTask[] epicTasks = gson.fromJson(jsonElementEpics.getAsJsonArray(), EpicTask[].class);
        assertEquals(2, epicTasks.length, "Неверное количество задач.");
        assertEquals(epicTasks[0], epicTask1, "Задачи не совпадают");
        assertEquals(epicTasks[1], epicTask2, "Задачи не совпадают");
    }

    @Test
    void testGETAllTasksShouldBeEmpty() {

        JsonElement jsonElementSubtasks = JsonParser.parseString(GET("subtask/"));
        SubTask[] subTasks = gson.fromJson(jsonElementSubtasks.getAsJsonArray(), SubTask[].class);
        assertEquals(0, subTasks.length, "Неверное количество задач.");
        JsonElement jsonElementTasks = JsonParser.parseString(GET("task/"));
        Task[] tasks = gson.fromJson(jsonElementTasks.getAsJsonArray(), Task[].class);
        assertEquals(0, tasks.length, "Неверное количество задач.");
        JsonElement jsonElementEpics = JsonParser.parseString(GET("epic/"));
        EpicTask[] epicTasks = gson.fromJson(jsonElementEpics.getAsJsonArray(), EpicTask[].class);
        assertEquals(0, epicTasks.length, "Неверное количество задач.");
    }

    @Test
    void testGETPrioritizedTasks() {
        task1.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(task2);
        taskManager.manageTaskObject(task3);
        taskManager.manageTaskObject(epicTask2);
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        subTask22.setEpicTaskId(epicTask2.getTaskId());
        subTask21.setTaskStatus(StatusType.DONE);
        subTask22.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask22);
        taskManager.manageTaskObject(epicTask3);
        subTask31.setEpicTaskId(epicTask3.getTaskId());
        subTask32.setEpicTaskId(epicTask3.getTaskId());
        subTask33.setEpicTaskId(epicTask3.getTaskId());
        taskManager.manageTaskObject(subTask31);
        taskManager.manageTaskObject(subTask32);
        taskManager.manageTaskObject(subTask33);
        URI url = URI.create(serverUrl + "/tasks/");
        String response;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response = resp.body();
            } else {
                throw new HttpClientException("Загрузка данных c сервера по ключу: tasks/ не удалась. Код ответа: " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных c сервера по ключу: tasks/ не удалась", e);
        }
        JsonElement jsonElementTasks = JsonParser.parseString(response);
        JsonArray array = jsonElementTasks.getAsJsonArray();

        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(array, taskType);

        for (Task task : tasks) {
            System.out.println(task);
        }
        assertEquals(9, tasks.size(), "Неверное количество задач");
        assertEquals(tasks.get(0), taskManager.getPrioritizedTasks().first(), "Задачи не совпадают");
    }

    @Test
    void testGETPrioritizedTasksShouldReturnEmptyList() {
        URI url = URI.create(serverUrl + "/tasks/");
        String response;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == HttpURLConnection.HTTP_OK) {
                response = resp.body();
            } else {
                throw new HttpClientException("Загрузка данных c сервера по ключу: tasks/ не удалась. Код ответа: " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных c сервера по ключу: tasks/ не удалась", e);
        }
        JsonElement jsonElementTasks = JsonParser.parseString(response);
        Task[] tasksArray = gson.fromJson(jsonElementTasks.getAsJsonArray(), Task[].class);
        assertEquals(0, tasksArray.length, "Список задач не пустой");
    }

    @Test
    void testDELETETaskById() {
        taskManager.manageTaskObject(task1);
        assertEquals(1, taskManager.getAllTasks().size(), "Список задач пуст");
        DELETE("task/?id=" + task1.getTaskId());
        assertEquals(0, taskManager.getAllTasks().size(), "задача не удалена");

        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        assertEquals(1, taskManager.getAllSubTasks().size(), "Список подзадач пуст");
        DELETE("subtask/?id=" + subTask11.getTaskId());
        assertEquals(0, taskManager.getAllSubTasks().size(), "задача не удалена");

        assertEquals(1, taskManager.getAllEpicTasks().size(), "Список эпиков пуст");
        DELETE("epic/?id=" + epicTask1.getTaskId());
        assertEquals(0, taskManager.getAllEpicTasks().size(), "задача не удалена");
        taskManager.manageTaskObject(epicTask2);
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        taskManager.manageTaskObject(subTask21);
        assertEquals(1, taskManager.getAllSubTasks().size(), "Список подзадач пуст");
        DELETE("subtask/epic/?id=" + epicTask2.getTaskId());
        assertEquals(0, taskManager.getAllSubTasks().size(), "задача не удалена");
    }

    @Test
    void testDELETETaskByIdShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        HttpClientException ex1 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("task/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: task/?id=123456789не удалась. " +
                "Код ответа: 404", ex1.getMessage());
        HttpClientException ex2 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("epic/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: epic/?id=123456789не удалась. " +
                "Код ответа: 404", ex2.getMessage());
        HttpClientException ex3 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("subtask/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: subtask/?id=123456789не удалась. " +
                "Код ответа: 404", ex3.getMessage());
        HttpClientException ex4 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("subtask/epic/?id=" + taskId)
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: subtask/epic/?id=123456789не удалась. " +
                "Код ответа: 404", ex4.getMessage());
    }

    @Test
    void testDELETEAllTasks() {
        task1.setDuration(Duration.ofMinutes(30));
        task2.setDuration(Duration.ofMinutes(30));
        task3.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime);
        task2.setStartTime(startTime.plusMinutes(30));
        task3.setStartTime(startTime.plusMinutes(60));
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(task2);
        taskManager.manageTaskObject(task3);
        List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        DELETE("task/");
        tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех задач не удалось");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task2)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task3)
                , "Временные метки не были удалены");
    }

    @Test
    void testDELETEAllEpicTasks() {
        taskManager.manageTaskObject(epicTask1);
        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        List<Task> tasks = taskManager.getAllEpicTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        DELETE("epic/");
        tasks = taskManager.getAllEpicTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех эпиков не удалось");
    }

    @Test
    void testDELETEAllSubTasks() {

        taskManager.manageTaskObject(epicTask1);
        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        subTask31.setEpicTaskId(epicTask3.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask31);
        List<Task> tasks = taskManager.getAllSubTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        DELETE("subtask/");
        tasks = taskManager.getAllSubTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех подзадач не удалось");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask11)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask21)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask31)
                , "Временные метки не были удалены");
    }

    @Test
    void testDELETEAllTasksShouldThrowExceptionWhenTasksMapsIsEmpty() {
        HttpClientException ex1 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("task/")
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: task/не удалась. " +
                "Код ответа: 404", ex1.getMessage());
        HttpClientException ex2 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("epic/")
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: epic/не удалась. " +
                "Код ответа: 404", ex2.getMessage());
        HttpClientException ex3 = Assertions.assertThrows(
                HttpClientException.class,
                () -> DELETE("subtask/")
        );
        Assertions.assertEquals("Загрузка данных c сервера по ключу: subtask/не удалась. " +
                "Код ответа: 404", ex3.getMessage());
    }

    public void POST(String key, String json) throws HttpClientException {
        URI url = URI.create(serverUrl + "/tasks/" + key);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(url)
                .build();

        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code != HttpURLConnection.HTTP_CREATED && code != HttpURLConnection.HTTP_ACCEPTED) {
                throw new HttpClientException("Загрузка данных на сервер по ключу: " + key +
                        "не удалась. Код ответа: " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных на сервер по ключу: " + key + "не удалась." + e.getMessage(), e);
        }
    }

    public String GET(String key) throws HttpClientException {
        URI url = URI.create(serverUrl + "/tasks/" + key);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == HttpURLConnection.HTTP_OK) return resp.body();
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key +
                    "не удалась. Код ответа: " + code);
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key + "не удалась", e);
        }
    }

    public String DELETE(String key) throws HttpClientException {
        URI url = URI.create(serverUrl + "/tasks/" + key);
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        try {
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == HttpURLConnection.HTTP_ACCEPTED) return resp.body();
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key +
                    "не удалась. Код ответа: " + code);
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key + "не удалась", e);
        }
    }
}