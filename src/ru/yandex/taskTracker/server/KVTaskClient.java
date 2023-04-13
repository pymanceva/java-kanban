package ru.yandex.taskTracker.server;

import ru.yandex.taskTracker.Exceptions.FailedLoadFromServerException;
import ru.yandex.taskTracker.Exceptions.FailedRegistrationException;
import ru.yandex.taskTracker.Exceptions.FailedSaveOnServerException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private static final String SERVER_URL = "http://localhost:8078/";
    private final String API_TOKEN; //не понимаю, как может быть константой,
    // модификатор static не подходит -
    // токен уникален для каждого клиента, то есть для каждого объекта класса


    public KVTaskClient(String url) {
        URI uri = URI.create(url + "register");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpRequest request = requestBuilder
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            API_TOKEN = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new FailedRegistrationException(exception);
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(SERVER_URL + "save/" + key + "/?API_TOKEN=" + API_TOKEN);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException exception) {
            throw new FailedSaveOnServerException(exception);
        }
    }

    public String load(String key) {
        URI uri = URI.create(SERVER_URL + "load/" + key + "/?API_TOKEN=" + API_TOKEN);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpRequest request = requestBuilder
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response;
        String body;
        try {
            response =
                    client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            body = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new FailedLoadFromServerException(exception);
        }
        return body;
    }
}
