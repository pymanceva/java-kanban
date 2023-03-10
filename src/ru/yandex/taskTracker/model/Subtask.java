package ru.yandex.taskTracker.model;

import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

public class Subtask extends Task {
    private final int idOfEpic;

    public Subtask(String name, String description, Status status, int idOfEpic, TaskType type) {
        super(name, description, status, type);
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," +
                idOfEpic;
    }
}
