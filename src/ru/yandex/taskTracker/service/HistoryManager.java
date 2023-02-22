package ru.yandex.taskTracker.service;

import ru.yandex.taskTracker.model.Task;
import java.util.ArrayList;

public interface HistoryManager {
    void addTask(Task task);
    void removeTask(int id);
    ArrayList<Task> getTasks();
}
