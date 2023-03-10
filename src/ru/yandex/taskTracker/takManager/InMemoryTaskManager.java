package ru.yandex.taskTracker.takManager;

import java.util.*;

import ru.yandex.taskTracker.historyManager.InMemoryHistoryManager;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;

import static ru.yandex.taskTracker.util.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;
    public final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            ++id;
            task.setId(id);
            tasks.put(id, task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            ++id;
            epic.setId(id);
            epics.put(id, epic);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask != null) {
            ++id;
            subtask.setId(id);
            subtasks.put(id, subtask);
            int idOfEpic = subtask.getIdOfEpic();
            Epic epic = epics.get(idOfEpic);
            List<Integer> subtaskOfEpic;
            if (epic.getSubtasksOfEpic() == null) {
                subtaskOfEpic = new ArrayList<>();
            } else {
                subtaskOfEpic = epic.getSubtasksOfEpic();
            }
            subtaskOfEpic.add(subtask.getId());
            epic.setSubtasksOfEpic(subtaskOfEpic);
            checkStatusOfEpic(epic);
        }
    }

    @Override
    public List<Integer> getListOfSubtasksOfEpic(int id) {
        return epics.get(id).getSubtasksOfEpic();
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                inMemoryHistoryManager.removeTask(id);
            }
            tasks.clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                inMemoryHistoryManager.removeTask(id);
            }
            epics.clear();
            deleteAllSubtasks();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Integer id : subtasks.keySet()) {
                inMemoryHistoryManager.removeTask(id);
            }
            subtasks.clear();
            for (Epic epic : epics.values()) {
                checkStatusOfEpic(epic);
            }
        }
    }

    @Override
    public void updateTask(Task task, int id) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
        } else {
            addTask(task);
        }
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        if (epic != null) {
            if (epics.containsKey(id)) {
                List<Integer> subtaskList = epics.get(id).getSubtasksOfEpic();
                epic.setId(id);
                epics.put(id, epic);
                epic.setSubtasksOfEpic(subtaskList);
                checkStatusOfEpic(epic);
            } else {
                addEpic(epic);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        if (subtask != null) {
            if (subtasks.containsKey(id)) {
                subtask.setId(id);
                subtasks.put(id, subtask);
                checkStatusOfEpic(epics.get(subtask.getIdOfEpic()));
            } else {
                addSubtask(subtask);
            }
        }
    }

    @Override
    public void changeStatusOfTask(Task task, Status status) {
        if (task != null) {
            task.setStatus(status);
        }
    }

    @Override
    public void changeStatusOfSubtask(Subtask subtask, Status status) {
        if (subtask != null) {
            subtask.setStatus(status);
            Epic epic = epics.get(subtask.getIdOfEpic());
            checkStatusOfEpic(epic);
        }
    }

    @Override
    public void checkStatusOfEpic(Epic epic) {
        if (epic != null) {
            List<Integer> subtaskList = epic.getSubtasksOfEpic();
            int newSubtask = 0;
            int inProgressSubtask = 0;
            int doneSubtask = 0;
            for (Integer idOfSubtask : subtaskList) {
                Subtask subtask = subtasks.get(idOfSubtask);
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
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        inMemoryHistoryManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        inMemoryHistoryManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.addTask(subtask);
        return subtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        inMemoryHistoryManager.removeTask(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        List<Integer> subtasksOfEpic = epics.get(id).getSubtasksOfEpic();
        for (Integer idSubtask : subtasksOfEpic) {
            inMemoryHistoryManager.removeTask(idSubtask);
            subtasks.remove(idSubtask);
        }
        inMemoryHistoryManager.removeTask(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        inMemoryHistoryManager.removeTask(id);
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
