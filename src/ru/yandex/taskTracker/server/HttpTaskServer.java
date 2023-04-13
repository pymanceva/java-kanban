package ru.yandex.taskTracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
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

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static int PORT = 8080;
    private final HttpServer server;
    final private Gson gson;
    final private FileBackedTasksManager manager;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        manager = new FileBackedTasksManager(Path.of("src/resources/kanban.csv").toFile());
    }

    public HttpTaskServer(FileBackedTasksManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        this.manager = manager;
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
                    h.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleGet(HttpExchange h, String path) {
        try {
            if (path.equals("/tasks/task/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getTasks());
                sendText(h, response);
            }

            if (path.equals("/tasks/subtask/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getSubtasks());
                sendText(h, response);
            }

            if (path.equals("/tasks/epic/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getEpics());
                sendText(h, response);
            }

            if (path.equals("/tasks/task/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("GET")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    String response = gson.toJson(manager.getTaskByID(id));
                    sendText(h, response);
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/subtask/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("GET")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    String response = gson.toJson(manager.getSubtaskByID(id));
                    sendText(h, response);
                    h.sendResponseHeaders(200, 0);
                    return;
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/epic/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("GET")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    String response = gson.toJson(manager.getEpicByID(id));
                    sendText(h, response);
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/") && h.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.getPrioritizedTasks());
                sendText(h, response);
            }

            if (path.equals("/tasks/history/") && h.getRequestMethod().equals("GET")) {
                String response = gson.toJson(manager.inMemoryHistoryManager.toString());
                sendText(h, response);
            }

            if (path.equals("/tasks/epic/subtask/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("GET")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    String response = gson.toJson(manager.getListOfSubtasksOfEpic(id));
                    sendText(h, response);
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handlePost(HttpExchange h, String path) {
        try {
            if (path.equals("/tasks/task/") && h.getRequestMethod().equals("POST")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Task task = gson.fromJson(body, Task.class);
                    manager.addTask(task);
                    System.out.println("Задача успешно добавлена");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/subtask/") && h.getRequestMethod().equals("POST")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    manager.addSubtask(subtask);
                    System.out.println("Субзадача успешно добавлена");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/epic/") && h.getRequestMethod().equals("POST")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Epic epic = gson.fromJson(body, Epic.class);
                    manager.addEpic(epic);
                    System.out.println("Эпик успешно добавлен");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handlePut(HttpExchange h, String path) {
        try {
            if (path.equals("/tasks/task/") && h.getRequestMethod().equals("PUT")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Task task = gson.fromJson(body, Task.class);
                    manager.updateTask(task, task.getId());
                    System.out.println("Задача успешно обновлена");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/subtask/") && h.getRequestMethod().equals("PUT")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    manager.updateTask(subtask, subtask.getId());
                    System.out.println("Субзадача успешно обновлена");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }

            if (path.equals("/tasks/epic/") && h.getRequestMethod().equals("PUT")) {
                InputStream inputStream = h.getRequestBody();
                String body = new String(inputStream.readAllBytes(), UTF_8);
                try {
                    Epic epic = gson.fromJson(body, Epic.class);
                    manager.updateEpic(epic, epic.getId());
                    System.out.println("Эпик успешно обновлен");
                    h.sendResponseHeaders(200, 0);
                } catch (JsonSyntaxException exception) {
                    System.out.println("Получен некорректный JSON");
                    h.sendResponseHeaders(405, 0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleDelete(HttpExchange h, String path) {
        try {
            if (path.equals("/tasks/task/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("DELETE")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    manager.deleteTaskByID(id);
                    System.out.println("Задача с ID " + id + " успешно удалена");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
                return;
            }

            if (path.equals("/tasks/subtask/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("DELETE")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    manager.deleteSubtaskByID(id);
                    System.out.println("Задача с ID " + id + " успешно удалена");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
                return;
            }

            if (path.equals("/tasks/epic/") && h.getRequestURI().getQuery() != null
                    && h.getRequestMethod().equals("DELETE")) {
                String pathId = h.getRequestURI().getQuery().split("=")[1];
                int id = parsePathId(pathId);
                if (id != -1) {
                    manager.deleteEpicByID(id);
                    System.out.println("Задача с ID " + id + " успешно удалена");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("Получени некорректный ID - " + pathId);
                    h.sendResponseHeaders(405, 0);
                }
                return;
            }

            if (path.equals("/tasks/task/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("DELETE")) {
                manager.deleteAllTasks();
                System.out.println("Все задачи успешно удалены");
                h.sendResponseHeaders(200, 0);
                return;
            }

            if (path.equals("/tasks/subtask/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("DELETE")) {
                manager.deleteAllSubtasks();
                System.out.println("Все подзадачи успешно удалены");
                h.sendResponseHeaders(200, 0);
                return;
            }

            if (path.equals("/tasks/epic/") && h.getRequestURI().getQuery() == null
                    && h.getRequestMethod().equals("DELETE")) {
                manager.deleteAllEpics();
                System.out.println("Все эпики успешно удалены");
                h.sendResponseHeaders(200, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            h.close();
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
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileBackedTasksManager getManager() {
        return manager;
    }
}
