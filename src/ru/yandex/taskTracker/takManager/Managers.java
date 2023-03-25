package ru.yandex.taskTracker.takManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTasksManager(file);
    }
}


