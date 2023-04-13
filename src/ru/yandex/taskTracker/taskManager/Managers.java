package ru.yandex.taskTracker.taskManager;

import ru.yandex.taskTracker.historyManager.HistoryManager;
import ru.yandex.taskTracker.historyManager.InMemoryHistoryManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTasksManager(file);
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}


