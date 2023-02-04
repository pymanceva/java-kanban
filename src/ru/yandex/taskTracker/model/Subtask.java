package ru.yandex.taskTracker.model;

import ru.yandex.taskTracker.service.Status;

public class Subtask extends Task {
    private int idOfEpic;

    public Subtask(String name, String description, Status status, int idOfEpic) {
        super(name, description, status);
        this.idOfEpic = idOfEpic;
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", idOfEpic=" + idOfEpic +
                '}';
    }
}
