package ru.yandex.taskTracker.model;

import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int idOfEpic;

    public Subtask(String name,
                   String description,
                   Status status,
                   int idOfEpic,
                   TaskType type,
                   int duration,
                   LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        return (super.toString() + "," + idOfEpic);
    }
}
