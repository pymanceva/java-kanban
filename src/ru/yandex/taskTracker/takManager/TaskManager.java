package ru.yandex.taskTracker.takManager;

import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface TaskManager {
    void addPrioritizedTask(Task task);

    TreeSet<Task> getPrioritizedTasks();

    boolean validateTask(Task task);

    void addTask(Task task);

    void addTaskExisted(Task task);

    void addEpicExisted(Epic epic);

    void addSubtaskExisted(Subtask subtask);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateEpicBySubtask(Subtask subtask);

    List<Integer> getListOfSubtasksOfEpic(int id);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void updateTask(Task task, int id);

    void updateEpic(Epic epic, int id);

    void updateSubtask(Subtask subtask, int id);

    void changeStatusOfTask(Task task, Status status);

    void changeStatusOfSubtask(Subtask subtask, Status status);

    void checkStatusOfEpic(Epic epic);

    void checkTimeOfEpic(Epic epic);

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubtaskByID(int id);

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

}
