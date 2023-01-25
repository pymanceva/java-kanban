package ru.yandex.taskTracker.model;

public class Subtask extends Task {
    int idOfEpic;

    public Subtask(String name, String description, String status, int idOfEpic) {
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
