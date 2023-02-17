package ru.mikhailantonov.taskmanager.server;

import com.sun.net.httpserver.HttpServer;
import ru.mikhailantonov.taskmanager.manager.tasks.TaskManager;
import ru.mikhailantonov.taskmanager.server.handlers.TaskHandler;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    public static final int PORT = 8081;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress("localhost", PORT), 0);
        TaskHandler taskHandler = new TaskHandler(taskManager);
        server.createContext("/tasks/", taskHandler);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(1);
    }
}
