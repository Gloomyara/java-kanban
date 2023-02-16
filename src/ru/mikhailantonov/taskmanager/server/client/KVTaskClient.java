package ru.mikhailantonov.taskmanager.server.client;

import ru.mikhailantonov.taskmanager.server.exceptions.HttpClientException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ru.mikhailantonov.taskmanager.server.enums.HttpCode.SUCCESS;

public class KVTaskClient {
    private final HttpClient kvServerClient = HttpClient.newHttpClient();
    private final String API_TOKEN;
    private final String serverUrl;

    public KVTaskClient(String serverUrl) throws HttpClientException {

        this.serverUrl = serverUrl;
        URI url = URI.create(serverUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        try {
            HttpResponse<String> apiToken = kvServerClient.send(request, HttpResponse.BodyHandlers.ofString());
            API_TOKEN = apiToken.body();
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Не могу получить данные от KVServer ", e);
        }
    }

    public KVTaskClient(String serverUrl, String apiToken) {
        this.serverUrl = serverUrl;
        this.API_TOKEN = apiToken;
    }

    public void put(String key, String json) throws HttpClientException {
        URI url = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(url)
                .build();

        try {
            HttpResponse<String> resp = kvServerClient.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code != SUCCESS.getCode()) {
                throw new HttpClientException("Загрузка данных на сервер по ключу: " + key +
                        "не удалась. Код ответа: " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных на сервер по ключу: " + key + "не удалась." + e.getMessage(), e);
        }
    }

    public String load(String key) throws HttpClientException {
        URI url = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        try {
            HttpResponse<String> resp = kvServerClient.send(request, HttpResponse.BodyHandlers.ofString());
            int code = resp.statusCode();
            if (code == SUCCESS.getCode()) return resp.body();
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key +
                    "не удалась. Код ответа: " + code);
        } catch (IOException | InterruptedException e) {
            throw new HttpClientException("Загрузка данных c сервера по ключу: " + key + "не удалась", e);
        }
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }
}
