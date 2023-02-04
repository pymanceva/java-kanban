package ru.yandex.taskTracker.service;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
