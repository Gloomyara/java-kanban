package ru.mikhailantonov.taskmanager.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.mikhailantonov.taskmanager.manager.tasks.TaskManager;
import ru.mikhailantonov.taskmanager.server.enums.HttpEndPoint;
import ru.mikhailantonov.taskmanager.server.enums.HttpMethod;
import ru.mikhailantonov.taskmanager.server.exceptions.EndPointException;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.exceptions.TimeStampsCrossingException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TaskHandler implements HttpHandler {
    private final Gson gson;
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = gsonBuilder.create();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            if (exchange.getRequestURI().getPath().endsWith("/tasks/")&&requestMethod.equals("GET")){
                String tasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                return;
            }
            HttpEndPoint endPoint = HttpEndPoint.fromString(exchange.getRequestURI().getPath().split("/")[2]);
            Optional<Integer> optionalTaskId;
            Integer taskId = null;
            boolean isContainsId = exchange.getRequestURI().toString().contains("?id=");
            if (isContainsId) {
                optionalTaskId = getTaskId(exchange);
                if (optionalTaskId.isEmpty()) {
                    throw new IllegalArgumentException("Ошибка при обработке запроса: " +
                            requestMethod + "! Передан некорректный id задачи.");
                }
                taskId = optionalTaskId.get();
            }
            switch (HttpMethod.valueOf(requestMethod)) {
                case GET:
                    if (isContainsId) {
                        handleGetTasksById(exchange, endPoint, taskId);
                    } else {
                        handleGetTasks(exchange, endPoint);
                    }
                    break;
                case POST:
                    handlePostTasks(exchange, endPoint);
                    break;
                case DELETE:
                    if (isContainsId) {
                        handleDeleteTasksById(exchange, endPoint, taskId);
                    } else {
                        handleDeleteTasks(exchange, endPoint);
                    }
                    break;
                default:
                    writeResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD,
                            "Метод не поддерживается" + requestMethod);
            }
        } catch (NoSuchElementException e) {
            writeResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | EndPointException | IndexOutOfBoundsException e) {
            writeResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, e.getMessage());
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "Получен некорректный JSON");
        } catch (Exception e) {
            writeResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGetTasksById(HttpExchange exchange, HttpEndPoint endPoint, Integer taskId)
            throws IOException, EndPointException, NoSuchElementException,
            IllegalArgumentException, JsonSyntaxException {

        String tasksJson;
        try {
            switch (endPoint) {
                case TASK_ENDPOINT:
                    tasksJson = gson.toJson(taskManager.getTask(taskId));
                    writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                    break;
                case SUBTASK_ENDPOINT:
                    if (exchange.getRequestURI().toString().contains("epic")) {
                        tasksJson = gson.toJson(taskManager.getOneEpicSubTasks(taskId));
                        writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                        break;
                    }
                    tasksJson = gson.toJson(taskManager.getSubTask(taskId));
                    writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                    break;
                case EPIC_ENDPOINT:
                    tasksJson = gson.toJson(taskManager.getEpicTask(taskId));
                    writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                    break;
                default:
                    throw new EndPointException("Ошибка при обработке метода GET!" +
                            " Эндпоинта: " + endPoint + " не существует");
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Ошибка при обработке метода GET! " + e.getMessage());
        }
    }

    private void handleGetTasks(HttpExchange exchange, HttpEndPoint endPoint)
            throws IOException, EndPointException, JsonSyntaxException {

        String tasksJson;
        switch (endPoint) {

            case TASK_ENDPOINT:
                tasksJson = gson.toJson(taskManager.getAllTasks());
                writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                break;
            case SUBTASK_ENDPOINT:
                tasksJson = gson.toJson(taskManager.getAllSubTasks());
                writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                break;
            case EPIC_ENDPOINT:
                tasksJson = gson.toJson(taskManager.getAllEpicTasks());
                writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                break;
            case HISTORY_ENDPOINT:
                tasksJson = gson.toJson(taskManager.getHistory());
                writeResponse(exchange, HttpURLConnection.HTTP_OK, tasksJson);
                break;
            default:
                throw new EndPointException("Ошибка при обработке метода GET!" +
                        " Эндпоинта: " + endPoint + " не существует");
        }
    }

    private void handleDeleteTasksById(HttpExchange exchange, HttpEndPoint endPoint, Integer taskId)
            throws IOException, EndPointException, NoSuchElementException,
            IllegalArgumentException {

        try {
            switch (endPoint) {
                case TASK_ENDPOINT:
                    taskManager.deleteTask(taskId);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Задача с id: " + taskId + " успешно удалена.");
                    break;
                case SUBTASK_ENDPOINT:
                    if (exchange.getRequestURI().toString().contains("epic")) {
                        if (taskManager.deleteOneEpicSubTasks(taskId)) {
                            writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "В эпике с ID: " + taskId + " все подзадачи удалены.");
                            break;
                        }
                        throw new NoSuchElementException("В эпике с ID: " + taskId + " нечего удалять.");
                    }
                    taskManager.deleteSubTask(taskId);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Подзадача с id: " + taskId + " успешно удалена.");
                    break;
                case EPIC_ENDPOINT:
                    taskManager.deleteEpicTask(taskId);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Эпик с id: " + taskId + " успешно удален.");
                    break;
                default:
                    throw new EndPointException("Ошибка при обработке метода DELETE!" +
                            " Эндпоинта: " + endPoint + " не существует");
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Ошибка при обработке метода DELETE! " + e.getMessage());
        }
    }

    private void handleDeleteTasks(HttpExchange exchange, HttpEndPoint endPoint)
            throws IOException, EndPointException, NoSuchElementException {

        switch (endPoint) {
            case TASK_ENDPOINT:
                if (taskManager.deleteAllTasks()) {
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Все задачи удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            case SUBTASK_ENDPOINT:
                if (taskManager.deleteAllSubTasks()) {
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Все подзадачи удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            case EPIC_ENDPOINT:
                if (taskManager.deleteAllEpicTasks()) {
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Все эпики удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            default:
                throw new EndPointException("Ошибка при обработке метода DELETE!" +
                        " Эндпоинта: " + endPoint + " не существует");
        }
    }

    private void handlePostTasks(HttpExchange exchange, HttpEndPoint endPoint) throws IOException, EndPointException,
            NoSuchElementException, IllegalArgumentException, JsonSyntaxException {

        try (InputStream inputStream = exchange.getRequestBody()) {
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            switch (endPoint) {
                case TASK_ENDPOINT:
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getTaskId() == null) {
                        taskManager.manageTask(task);
                        writeResponse(exchange, HttpURLConnection.HTTP_CREATED, "Задача успешно добавлена. " +
                                "Присвоено id: " + task.getTaskId());
                        break;
                    }
                    taskManager.manageTask(task);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Задача c id: " +
                            task.getTaskId() + " успешно обновлена.");
                    break;
                case SUBTASK_ENDPOINT:
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (subTask.getTaskId() == null) {
                        taskManager.manageSubTask(subTask);
                        writeResponse(exchange, HttpURLConnection.HTTP_CREATED, "Подзадача успешно добавлена. " +
                                "Присвоено id:" + subTask.getTaskId());
                        break;
                    }
                    taskManager.manageSubTask(subTask);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Подзадача c id: " +
                            subTask.getTaskId() + " успешно обновлена.");
                    break;

                case EPIC_ENDPOINT:
                    EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                    if (epicTask.getTaskId() == null) {
                        taskManager.manageEpicTask(epicTask);
                        writeResponse(exchange, HttpURLConnection.HTTP_CREATED, "Эпик успешно добавлен. " +
                                "Присвоено id: " + epicTask.getTaskId());
                        break;
                    }
                    taskManager.manageEpicTask(epicTask);
                    writeResponse(exchange, HttpURLConnection.HTTP_ACCEPTED, "Эпик c id: " +
                            epicTask.getTaskId() + "успешно обновлен.");
                    break;
                default:
                    throw new EndPointException("Ошибка при обработке метода POST!" +
                            " Эндпоинта: " + endPoint + " не существует");
            }
        } catch (TimeStampsCrossingException | NullPointerException e) {
            throw new IllegalArgumentException("Ошибка при обработке метода POST! " + e.getMessage());
        }
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] resp = response.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, resp.length);
        exchange.getResponseBody().write(resp);
    }
}
