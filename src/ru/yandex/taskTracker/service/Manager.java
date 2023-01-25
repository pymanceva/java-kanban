package ru.yandex.taskTracker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;

public class Manager {
    Map<Integer, Task> tasks = new HashMap<>();
    Map<Integer, Epic> epics = new HashMap<>();
    Map<Integer, Subtask> subtasks = new HashMap<>();
    int id = 0;
    public final String NEW = "NEW";
    public final String IN_PROGRESS = "IN_PROGRESS";
    public final String DONE = "DONE";

    public void addTask(Task task) {
        ++id;
        task.setId(id);
        tasks.put(id, task);
    }

    public void addEpic(Epic epic) {
        ++id;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void addSubtask(Subtask subtask) {
        ++id;
        subtask.setId(id);
        subtasks.put(id, subtask);
        int idOfEpic = subtask.getIdOfEpic();
        Epic epic = epics.get(idOfEpic);
        List<Subtask> subtaskOfEpic = epic.getSubtasksOfEpic();
        subtaskOfEpic.add(subtask);
        epic.setSubtasksOfEpic(subtaskOfEpic);
        checkStatusOfEpic(epic);
    }

    public List<Subtask> getListOfSubtasksOfEpic(int id) {
        return epics.get(id).getSubtasksOfEpic();
    }

    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            deleteAllSubtasks();
        }
    }

    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                checkStatusOfEpic(epic);
            }
        }
    }

    public void updateTask(Task task, int id) {
        task.setId(id);
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic, int id) {
        List<Subtask> subtaskList = epics.get(id).getSubtasksOfEpic();
        epic.setId(id);
        epics.put(id, epic);
        epic.setSubtasksOfEpic(subtaskList);
        checkStatusOfEpic(epic);
    }

    public void updateSubtask(Subtask subtask, int id) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        checkStatusOfEpic(epics.get(subtask.getIdOfEpic()));
    }

    public void changeStatusOfTask(Task task, String status) {
        task.setStatus(status);
    }

    public void changeStatusOfSubtask(Subtask subtask, String status) {
        subtask.setStatus(status);
        Epic epic = epics.get(subtask.getIdOfEpic());
        checkStatusOfEpic(epic);
    }

    public void checkStatusOfEpic(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasksOfEpic();
        int newSubtask = 0;
        int inProgressSubtask = 0;
        int doneSubtask = 0;
        for (Subtask subtask : subtaskList) {
            switch (subtask.getStatus()) {
                case "NEW":
                    ++newSubtask;
                    break;
                case "IN_PROGRESS":
                    ++inProgressSubtask;
                    break;
                case "DONE":
                    ++doneSubtask;
            }
        }
        if (inProgressSubtask == 0 && doneSubtask == 0) {
            epic.setStatus("NEW");
        } else if (!subtaskList.isEmpty() && newSubtask == 0 && inProgressSubtask == 0) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(int id) {
        List<Subtask> subtasksOfEpic = epics.get(id).getSubtasksOfEpic();
        for (Subtask subtask : subtasksOfEpic) {
            int idSubtask = subtask.getId();
            subtasks.remove(idSubtask);
        }
        epics.remove(id);
    }

    public void deleteSubtaskByID(int id) {
        subtasks.remove(id);
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}
