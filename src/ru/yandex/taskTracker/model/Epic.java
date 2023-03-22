package ru.yandex.taskTracker.model;

import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksOfEpic = new ArrayList<>();

    public Epic(String name, String description, Status status, TaskType type) {
        super(name, description, status, type, 0, null);
    }

    public List<Integer> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    public void setSubtasksOfEpic(List<Integer> subtasksOfEpic) {
        this.subtasksOfEpic = subtasksOfEpic;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
