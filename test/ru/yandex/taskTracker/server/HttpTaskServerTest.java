package ru.yandex.taskTracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.taskManager.FileBackedTasksManager;
import ru.yandex.taskTracker.util.LocalDateTimeAdapter;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    private final String URL = "http://localhost:8080/";
    private HttpTaskServer server;
    private Task task1;
    private Epic epic2;
    private Subtask subtask3;
    private HttpClient client;
    private Gson gson;
    private FileBackedTasksManager manager;

    @BeforeEach
    void startServer() throws IOException {
        server = new HttpTaskServer();
        server.start();
        task1 = new Task("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        epic2 = new Epic("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.EPIC);
        subtask3 = new Subtask("TestTaskName", "TestTaskDescription", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 10));
        client = HttpClient.newHttpClient();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
        manager = server.getManager();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException {
        manager.addTask(task1);
        URI uri = URI.create(URL + "tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        Map<Integer, Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Количество задач не совпадает");
        assertEquals(task1, actual.get(1), "Задачи не совпадают");
    }

    @Test
    void shouldReturnAllSubtasks() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType();
        Map<Integer, Subtask> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Количество задач не совпадает");
        assertEquals(subtask3, actual.get(2), "Задачи не совпадают");
    }

    @Test
    void shouldReturnAllEpics() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        URI uri = URI.create(URL + "tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<HashMap<Integer, Epic>>() {
        }.getType();
        Map<Integer, Epic> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(1, actual.size(), "Количество задач не совпадает");
    }

    @Test
    void shouldReturnTask() throws IOException, InterruptedException {
        manager.addTask(task1);
        URI uri = URI.create(URL + "tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(task1, actual, "Задачи не совпадают");
    }

    @Test
    void shouldReturnEpic() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        URI uri = URI.create(URL + "tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
    }

    @Test
    void shouldReturnSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(subtask3, actual, "Задачи не совпадают");
    }

    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        manager.addTask(task1);
        manager.getTaskByID(3);
        manager.getEpicByID(1);
        String list = manager.inMemoryHistoryManager.toString();
        URI uri = URI.create(URL + "tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<String>() {
        }.getType();
        String actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются");
        assertEquals(list, actual, "Задачи не совпадают");
    }

    @Test
    void shouldReturnCode200WhenDeleteTasks() throws IOException, InterruptedException {
        manager.addTask(task1);
        URI uri = URI.create(URL + "tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenDeleteSubtasks() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenDeleteEpics() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenDeleteTask() throws IOException, InterruptedException {
        manager.addTask(task1);
        URI uri = URI.create(URL + "tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenDeleteSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenDeleteEpic() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenUpdateTask() throws IOException, InterruptedException {
        manager.addTask(task1);
        Task newTask = new Task("NewTestTaskName", "NewTestTaskDescription", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        newTask.setId(1);
        String jsonNewTask = gson.toJson(newTask);
        URI uri = URI.create(URL + "tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNewTask))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenUpdateSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/");
        Subtask newSubtask = new Subtask("NewTestTaskName", "NewTestTaskDescription", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2022, 1, 1, 0, 10));
        newSubtask.setId(2);
        String jsonNewSubtask = gson.toJson(newSubtask);
        client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNewSubtask))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenUpdateEpic() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        URI uri = URI.create(URL + "tasks/epic/");
        Epic newEpic = new Epic("NewTestTaskName", "NewTestTaskDescription", Status.NEW, TaskType.EPIC);
        newEpic.setId(1);
        String jsonNewEpic = gson.toJson(newEpic);
        client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .PUT(HttpRequest.BodyPublishers.ofString(jsonNewEpic))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenAddTask() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(task1);
        URI uri = URI.create(URL + "tasks/task/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenAddSubtask() throws IOException, InterruptedException {
        manager.addEpic(epic2);
        String jsonSubtask = gson.toJson(subtask3);
        URI uri = URI.create(URL + "tasks/subtask/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void shouldReturnCode200WhenAddEpic() throws IOException, InterruptedException {
        String jsonEpic = gson.toJson(epic2);
        URI uri = URI.create(URL + "tasks/epic/");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}
