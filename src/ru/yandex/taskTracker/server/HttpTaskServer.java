package ru.yandex.taskTracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskTracker.Exceptions.CreateServerException;
import ru.yandex.taskTracker.Exceptions.SendResponseHeadersException;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.taskManager.FileBackedTasksManager;
import ru.yandex.taskTracker.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.net.HttpURLConnection;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    final private Gson gson;
    final private FileBackedTasksManager manager;

    public HttpTaskServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/tasks", this::handleTasks);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            gson = gsonBuilder.create();
            manager = new FileBackedTasksManager(Path.of("src/resources/kanban.csv").toFile());
        } catch (IOException e) {
            throw new CreateServerException(e);
        }
    }

    public HttpTaskServer(FileBackedTasksManager manager) {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/tasks", this::handleTasks);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            gson = gsonBuilder.create();
            this.manager = manager;
        } catch (IOException e) {
            throw new CreateServerException(e);
        }
    }

    private void handleTasks(HttpExchange h) {
        try {
            String path = h.getRequestURI().getPath();
            String requestMethod = h.getRequestMethod();
            switch (requestMethod) {
                case "GET": {
                    handleGet(h, path);
                    break;
                }
                case "POST": {
                    handlePost(h, path);
                    break;
                }
                case "PUT": {
                    handlePut(h, path);
                    break;
                }
                case "DELETE": {
                    handleDelete(h, path);
                    break;
                }
                default: {
                    h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
                }
            }
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        } finally {
            h.close();
        }
    }

    private void handleGet(HttpExchange h, String path) {
        try {
            switch (path) {
                case "/tasks/task/":
                    this.handleGetTasks(h);
                    break;
                case "/tasks/subtask/":
                    this.handleGetSubtasks(h);
                    break;
                case "/tasks/epic/":
                    this.handleGetEpics(h);
                    break;
                case "/tasks/":
                    this.handleGetAllTasks(h);
                    break;
                case "/tasks/history/":
                    this.handleGetHistory(h);
                    break;
                case "/tasks/epic/subtask/":
                    this.handleGetSubtasksOfEpic(h);
                    break;
            }
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        } finally {
            h.close();
        }
    }

    private void handleGetTasks(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("GET")) {
            String response = gson.toJson(manager.getTasks());
            sendText(h, response);
        }

        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("GET")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getTaskByID(id));
                sendText(h, response);
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handleGetSubtasks(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("GET")) {
            String response = gson.toJson(manager.getSubtasks());
            sendText(h, response);
        }

        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("GET")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getSubtaskByID(id));
                sendText(h, response);
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handleGetEpics(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("GET")) {
            String response = gson.toJson(manager.getEpics());
            sendText(h, response);
        }

        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("GET")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getEpicByID(id));
                sendText(h, response);
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handleGetAllTasks(HttpExchange h) {
        if (h.getRequestMethod().equals("GET")) {
            String response = gson.toJson(manager.getPrioritizedTasks());
            sendText(h, response);
        }
    }

    private void handleGetHistory(HttpExchange h) {
        if (h.getRequestMethod().equals("GET")) {
            String response = gson.toJson(manager.inMemoryHistoryManager.toString());
            sendText(h, response);
        }
    }

    private void handleGetSubtasksOfEpic(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("GET")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getListOfSubtasksOfEpic(id));
                sendText(h, response);
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handlePost(HttpExchange h, String path) {
        try {
            switch (path) {
                case "/tasks/task/":
                    this.handlePostTask(h);
                    break;
                case "/tasks/subtask/":
                    this.handlePostSubtask(h);
                    break;
                case "/tasks/epic/":
                    this.handlePostEpic(h);
                    break;
            }
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        } finally {
            h.close();
        }
    }

    private void handlePostTask(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("POST")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Task task = gson.fromJson(body, Task.class);
                manager.addTask(task);
                System.out.println("Задача успешно добавлена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handlePostSubtask(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("POST")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                manager.addSubtask(subtask);
                System.out.println("Субзадача успешно добавлена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handlePostEpic(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("POST")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Epic epic = gson.fromJson(body, Epic.class);
                manager.addEpic(epic);
                System.out.println("Эпик успешно добавлен");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }


    private void handlePut(HttpExchange h, String path) {
        try {
            switch (path) {
                case "/tasks/task/":
                    this.handlePutTask(h);
                    break;
                case "/tasks/subtask/":
                    this.handlePutSubtask(h);
                    break;
                case "/tasks/epic/":
                    this.handlePutEpic(h);
                    break;
            }
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        } finally {
            h.close();
        }
    }

    private void handlePutTask(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("PUT")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Task task = gson.fromJson(body, Task.class);
                manager.updateTask(task, task.getId());
                System.out.println("Задача успешно обновлена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handlePutSubtask(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("PUT")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Subtask subtask = gson.fromJson(body, Subtask.class);
                manager.updateTask(subtask, subtask.getId());
                System.out.println("Субзадача успешно обновлена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handlePutEpic(HttpExchange h) throws IOException {
        if (h.getRequestMethod().equals("PUT")) {
            InputStream inputStream = h.getRequestBody();
            String body = new String(inputStream.readAllBytes(), UTF_8);
            try {
                Epic epic = gson.fromJson(body, Epic.class);
                manager.updateEpic(epic, epic.getId());
                System.out.println("Эпик успешно обновлен");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } catch (JsonSyntaxException exception) {
                System.out.println("Получен некорректный JSON");
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }
    }

    private void handleDelete(HttpExchange h, String path) {
        try {
            switch (path) {
                case "/tasks/task/":
                    this.handleDeleteTasks(h);
                    break;
                case "/tasks/subtask/":
                    this.handleDeleteSubtasks(h);
                    break;
                case "/tasks/epic/":
                    this.handleDeleteEpic(h);
                    break;
            }
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        } finally {
            h.close();
        }
    }

    private void handleDeleteTasks(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("DELETE")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteTaskByID(id);
                System.out.println("Задача с ID " + id + " успешно удалена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }

        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("DELETE")) {
            manager.deleteAllTasks();
            System.out.println("Все задачи успешно удалены");
            h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }
    }

    private void handleDeleteSubtasks(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("DELETE")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteSubtaskByID(id);
                System.out.println("Задача с ID " + id + " успешно удалена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }

        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("DELETE")) {
            manager.deleteAllSubtasks();
            System.out.println("Все подзадачи успешно удалены");
            h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }
    }

    private void handleDeleteEpic(HttpExchange h) throws IOException {
        if (h.getRequestURI().getQuery() != null && h.getRequestMethod().equals("DELETE")) {
            String pathId = h.getRequestURI().getQuery().split("=")[1];
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteEpicByID(id);
                System.out.println("Задача с ID " + id + " успешно удалена");
                h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            } else {
                System.out.println("Получени некорректный ID - " + pathId);
                h.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            }
        }

        if (h.getRequestURI().getQuery() == null && h.getRequestMethod().equals("DELETE")) {
            manager.deleteAllEpics();
            System.out.println("Все эпики успешно удалены");
            h.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }
    }

    private int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    protected void sendText(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json");
            h.sendResponseHeaders(HttpURLConnection.HTTP_OK, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new SendResponseHeadersException(e);
        }
    }

    public FileBackedTasksManager getManager() {
        return manager;
    }
}
