package ru.yandex.taskTracker.taskManager;

import com.google.gson.*;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.server.KVTaskClient;
import ru.yandex.taskTracker.util.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {
    private final KVTaskClient client;
    private final String KEY_TASKS = "tasks";
    private final String KEY_EPICS = "epics";
    private final String KEY_SUBTASKS = "subtasks";
    private final String KEY_HISTORY = "history";
    private final Gson gson;

    public HttpTaskManager(String url) {
        super(null);
        client = new KVTaskClient(url);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gson = gsonBuilder.create();
    }

    public static HttpTaskManager loadFromServer() throws IOException, InterruptedException {
        HttpTaskManager loadedManager = new HttpTaskManager("http://localhost:8078/");
        JsonElement allTasksJson = JsonParser.parseString(loadedManager.client.load(loadedManager.KEY_TASKS));
        JsonArray allTasksJsonArray = allTasksJson.getAsJsonArray();
        for (JsonElement element : allTasksJsonArray) {
            Task task = loadedManager.gson.fromJson(element, Task.class);
            loadedManager.addTaskExisted(task);
        }

        JsonElement allSubtasksJson = JsonParser.parseString(loadedManager.client.load(loadedManager.KEY_SUBTASKS));
        JsonArray allSubtasksJsonArray = allSubtasksJson.getAsJsonArray();
        for (JsonElement element : allSubtasksJsonArray) {
            Subtask subtask = loadedManager.gson.fromJson(element, Subtask.class);
            loadedManager.addSubtaskExisted(subtask);
        }

        JsonElement allEpicsJson = JsonParser.parseString(loadedManager.client.load(loadedManager.KEY_EPICS));
        JsonArray allEpicsJsonArray = allEpicsJson.getAsJsonArray();
        for (JsonElement element : allEpicsJsonArray) {
            Epic epic = loadedManager.gson.fromJson(element, Epic.class);
            loadedManager.addEpicExisted(epic);
        }

        JsonElement historyJson = JsonParser.parseString(loadedManager.client.load(loadedManager.KEY_HISTORY));
        String history = historyJson.toString();
        history = history.replaceAll("\"", "");
        String[] parts = history.split(",");
        for (String idAsString : parts) {
            int id = Integer.parseInt(idAsString);
            if (loadedManager.tasks.containsKey(id)) {
                loadedManager.getTaskByID(id);
            } else if (loadedManager.subtasks.containsKey(id)) {
                loadedManager.getSubtaskByID(id);
            } else if (loadedManager.epics.containsKey(id)) {
                loadedManager.getEpicByID(id);
            }
        }
        return loadedManager;
    }

    @Override
    void save() {
        client.put(KEY_TASKS, gson.toJson(tasks.values()));
        client.put(KEY_SUBTASKS, gson.toJson(subtasks.values()));
        client.put(KEY_EPICS, gson.toJson(epics.values()));
        client.put(KEY_HISTORY, gson.toJson(historyToString(inMemoryHistoryManager)));
    }
}

