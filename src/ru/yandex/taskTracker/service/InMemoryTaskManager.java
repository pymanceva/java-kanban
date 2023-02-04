package ru.yandex.taskTracker.service;

import java.util.*;

import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;

import static ru.yandex.taskTracker.service.Status.*;

public class InMemoryTaskManager implements TaskManager{
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Override
    public void addTask(Task task) {
        ++id;
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void addEpic(Epic epic) {
        ++id;
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
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
    @Override
    public List<Subtask> getListOfSubtasksOfEpic(int id) {
        return epics.get(id).getSubtasksOfEpic();
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            deleteAllSubtasks();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                checkStatusOfEpic(epic);
            }
        }
    }

    @Override
    public void updateTask(Task task, int id) {
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        List<Subtask> subtaskList = epics.get(id).getSubtasksOfEpic();
        epic.setId(id);
        epics.put(id, epic);
        epic.setSubtasksOfEpic(subtaskList);
        checkStatusOfEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        subtask.setId(id);
        subtasks.put(id, subtask);
        checkStatusOfEpic(epics.get(subtask.getIdOfEpic()));
    }

    @Override
    public void changeStatusOfTask(Task task, Status status) {
        task.setStatus(status);
    }

    @Override
    public void changeStatusOfSubtask(Subtask subtask, Status status) {
        subtask.setStatus(status);
        Epic epic = epics.get(subtask.getIdOfEpic());
        checkStatusOfEpic(epic);
    }

    @Override
    public void checkStatusOfEpic(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasksOfEpic();
        int newSubtask = 0;
        int inProgressSubtask = 0;
        int doneSubtask = 0;
        for (Subtask subtask : subtaskList) {
            switch (subtask.getStatus()) {
                case NEW:
                    ++newSubtask;
                    break;
                case IN_PROGRESS:
                    ++inProgressSubtask;
                    break;
                case DONE:
                    ++doneSubtask;
            }
        }
        if (inProgressSubtask == 0 && doneSubtask == 0) {
            epic.setStatus(NEW);
        } else if (!subtaskList.isEmpty() && newSubtask == 0 && inProgressSubtask == 0) {
            epic.setStatus(DONE);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.add(subtask);
        return subtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        List<Subtask> subtasksOfEpic = epics.get(id).getSubtasksOfEpic();
        for (Subtask subtask : subtasksOfEpic) {
            int idSubtask = subtask.getId();
            subtasks.remove(idSubtask);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        checkStatusOfEpic(getEpicByID(subtask.getIdOfEpic()));
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}
