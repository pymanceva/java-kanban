package ru.yandex.taskTracker.service;

import ru.yandex.taskTracker.model.Task;

import java.util.List;

public interface HistoryManager {
    void addTask(Task task);
    List<Task> getHistory();
}
