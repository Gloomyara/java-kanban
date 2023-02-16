package ru.mikhailantonov.taskmanager.server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.mikhailantonov.taskmanager.manager.tasks.TaskManager;
import ru.mikhailantonov.taskmanager.server.enums.HttpMethod;
import ru.mikhailantonov.taskmanager.server.exceptions.EndPointException;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.exceptions.TimeStampsCrossingException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.mikhailantonov.taskmanager.server.enums.HttpCode.*;


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
            String endPoint = exchange.getRequestURI().getPath().split("/")[2].toLowerCase();
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
                    writeResponse(exchange, METHOD_NOT_ALLOWED.getCode(),
                            "Метод не поддерживается" + requestMethod);
            }
        } catch (NoSuchElementException e) {
            writeResponse(exchange, NOT_FOUND.getCode(), e.getMessage());
        } catch (IllegalArgumentException | EndPointException | IndexOutOfBoundsException e) {
            writeResponse(exchange, BAD_REQUEST.getCode(), e.getMessage());
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, BAD_REQUEST.getCode(), "Получен некорректный JSON");
        } catch (Exception e) {
            writeResponse(exchange, INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGetTasksById(HttpExchange exchange, String endPoint, Integer taskId)
            throws IOException, EndPointException, NoSuchElementException,
            IllegalArgumentException, JsonSyntaxException {

        String tasksJson;
        try {
            switch (endPoint) {
                case ("task"):
                    tasksJson = gson.toJson(taskManager.getTask(taskId));
                    writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                    break;
                case ("subtask"):
                    if (exchange.getRequestURI().toString().contains("epic")) {
                        tasksJson = gson.toJson(taskManager.getOneEpicSubTasks(taskId));
                        writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                        break;
                    }
                    tasksJson = gson.toJson(taskManager.getSubTask(taskId));
                    writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                    break;
                case ("epic"):
                    tasksJson = gson.toJson(taskManager.getEpicTask(taskId));
                    writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                    break;
                default:
                    throw new EndPointException("Ошибка при обработке метода GET!" +
                            " Эндпоинта: " + endPoint + " не существует");
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Ошибка при обработке метода GET! " + e.getMessage());
        }
    }

    private void handleGetTasks(HttpExchange exchange, String endPoint)
            throws IOException, EndPointException, JsonSyntaxException {

        String tasksJson;
        switch (endPoint) {
            case ("prioritized"):
                tasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                break;
            case ("task"):
                tasksJson = gson.toJson(taskManager.getAllTasks());
                writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                break;
            case ("subtask"):
                tasksJson = gson.toJson(taskManager.getAllSubTasks());
                writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                break;
            case ("epic"):
                tasksJson = gson.toJson(taskManager.getAllEpicTasks());
                writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                break;
            case ("history"):
                tasksJson = gson.toJson(taskManager.getHistory());
                writeResponse(exchange, SUCCESS.getCode(), tasksJson);
                break;
            default:
                throw new EndPointException("Ошибка при обработке метода GET!" +
                        " Эндпоинта: " + endPoint + " не существует");
        }
    }

    private void handleDeleteTasksById(HttpExchange exchange, String endPoint, Integer taskId)
            throws IOException, EndPointException, NoSuchElementException,
            IllegalArgumentException {

        try {
            switch (endPoint) {
                case ("task"):
                    taskManager.deleteTask(taskId);
                    writeResponse(exchange, ACCEPTED.getCode(), "Задача с id: " + taskId + " успешно удалена.");
                    break;
                case ("subtask"):
                    if (exchange.getRequestURI().toString().contains("epic")) {
                        if (taskManager.deleteOneEpicSubTasks(taskId)) {
                            writeResponse(exchange, ACCEPTED.getCode(), "В эпике с ID: " + taskId + " все подзадачи удалены.");
                            break;
                        }
                        throw new NoSuchElementException("В эпике с ID: " + taskId + " нечего удалять.");
                    }
                    taskManager.deleteSubTask(taskId);
                    writeResponse(exchange, ACCEPTED.getCode(), "Подзадача с id: " + taskId + " успешно удалена.");
                    break;
                case ("epic"):
                    taskManager.deleteEpicTask(taskId);
                    writeResponse(exchange, ACCEPTED.getCode(), "Эпик с id: " + taskId + " успешно удален.");
                    break;
                default:
                    throw new EndPointException("Ошибка при обработке метода DELETE!" +
                            " Эндпоинта: " + endPoint + " не существует");
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Ошибка при обработке метода DELETE! " + e.getMessage());
        }
    }

    private void handleDeleteTasks(HttpExchange exchange, String endPoint)
            throws IOException, EndPointException, NoSuchElementException {

        switch (endPoint) {
            case ("task"):
                if (taskManager.deleteAllTasks()) {
                    writeResponse(exchange, ACCEPTED.getCode(), "Все задачи удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            case ("subtask"):
                if (taskManager.deleteAllSubTasks()) {
                    writeResponse(exchange, ACCEPTED.getCode(), "Все подзадачи удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            case ("epic"):
                if (taskManager.deleteAllEpicTasks()) {
                    writeResponse(exchange, ACCEPTED.getCode(), "Все эпики удалены.");
                    break;
                }
                throw new NoSuchElementException("Нечего удалять.");
            default:
                throw new EndPointException("Ошибка при обработке метода DELETE!" +
                        " Эндпоинта: " + endPoint + " не существует");
        }
    }

    private void handlePostTasks(HttpExchange exchange, String endPoint) throws IOException, EndPointException,
            NoSuchElementException, IllegalArgumentException, JsonSyntaxException {

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            switch (endPoint) {
                case ("task"):
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getTaskId() == null) {
                        taskManager.manageTask(task);
                        writeResponse(exchange, CREATED.getCode(), "Задача успешно добавлена. " +
                                "Присвоено id: " + task.getTaskId());
                        break;
                    }
                    taskManager.manageTask(task);
                    writeResponse(exchange, ACCEPTED.getCode(), "Задача c id: " +
                            task.getTaskId() + " успешно обновлена.");
                    break;
                case ("subtask"):
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (subTask.getTaskId() == null) {
                        taskManager.manageSubTask(subTask);
                        writeResponse(exchange, CREATED.getCode(), "Подзадача успешно добавлена. " +
                                "Присвоено id:" + subTask.getTaskId());
                        break;
                    }
                    taskManager.manageSubTask(subTask);
                    writeResponse(exchange, ACCEPTED.getCode(), "Подзадача c id: " +
                            subTask.getTaskId() + " успешно обновлена.");
                    break;

                case ("epic"):
                    EpicTask epicTask = gson.fromJson(body, EpicTask.class);
                    if (epicTask.getTaskId() == null) {
                        taskManager.manageEpicTask(epicTask);
                        writeResponse(exchange, CREATED.getCode(), "Эпик успешно добавлен. " +
                                "Присвоено id: " + epicTask.getTaskId());
                        break;
                    }
                    taskManager.manageEpicTask(epicTask);
                    writeResponse(exchange, ACCEPTED.getCode(), "Эпик c id: " +
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
