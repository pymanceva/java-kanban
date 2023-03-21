package ru.yandex.taskTracker.model;

import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksOfEpic = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status, TaskType type, int duration, LocalDateTime startTime) {
        super(name, description, status, type, duration, startTime);
    }

    public List<Integer> getSubtasksOfEpic() {
        return subtasksOfEpic;
    }

    public void setSubtasksOfEpic(List<Integer> subtasksOfEpic) {
        this.subtasksOfEpic = subtasksOfEpic;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
