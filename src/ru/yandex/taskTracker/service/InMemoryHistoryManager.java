package ru.yandex.taskTracker.service;

import ru.yandex.taskTracker.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected static final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        history.add(task);
        checkHistory();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    void checkHistory() {
        if (history.size() > 10) {
            history.remove(0);
        }
    }
}
