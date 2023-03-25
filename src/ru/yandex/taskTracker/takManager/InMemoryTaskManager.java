package ru.yandex.taskTracker.takManager;

import ru.yandex.taskTracker.historyManager.InMemoryHistoryManager;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskManagerException;

import java.time.LocalDateTime;
import java.util.*;

import static ru.yandex.taskTracker.util.Status.*;

public class InMemoryTaskManager implements TaskManager {
    public final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);
    private int id = 0;


    @Override
    public void addTaskExisted(Task task) {
        if (task != null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void addEpicExisted(Epic epic) {
        if (epic != null) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void addSubtaskExisted(Subtask subtask) {
        if (subtask != null) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public boolean validateTask(Task task) {
        boolean isAvailable = false;
        if (task != null) {
            if (prioritizedTasks.isEmpty()) {
                isAvailable = true;
            } else if (task.getEndTime().isBefore(prioritizedTasks.first().getStartTime()) ||
                    task.getStartTime().isAfter(prioritizedTasks.last().getEndTime())) {
                isAvailable = true;
            } else {
                for (Task taskExisted : prioritizedTasks) {
                    if (task.getStartTime().isAfter(taskExisted.getEndTime()) &&
                            task.getEndTime().isBefore(prioritizedTasks.higher(taskExisted).getStartTime())) {
                        isAvailable = true;
                    }
                }
            }
        }
        return isAvailable;
    }

    @Override
    public void addPrioritizedTask(Task task) {
        if (task != null) {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            } else {
                LocalDateTime tempStartTime = prioritizedTasks.last().getEndTime().plusMinutes(1);
                task.setStartTime(tempStartTime);
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (validateTask(task)) {
                ++id;
                task.setId(id);
                tasks.put(id, task);
                addPrioritizedTask(task);
            } else {
                throw new TaskManagerException("Нельзя выполнять более 1 задачи за раз");
            }
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
            if (validateTask(subtask)) {
                if (epics.containsKey(subtask.getIdOfEpic())) {
                    ++id;
                    subtask.setId(id);
                    subtasks.put(id, subtask);
                    addPrioritizedTask(subtask);
                    updateEpicBySubtask(subtask);
                } else {
                    throw new TaskManagerException("Эпик, которому принадлежит эта задача, не найден");
                }
            } else {
                throw new TaskManagerException("Нельзя выполнять более 1 задачи за раз");
            }
        }
    }

    @Override
    public void updateEpicBySubtask(Subtask subtask) {
        if (subtask != null) {
            int idOfEpic = subtask.getIdOfEpic();
            Epic epic = epics.get(idOfEpic);
            List<Integer> subtaskOfEpic;
            try {
                if (epic.getSubtasksOfEpic() == null) {
                    subtaskOfEpic = new ArrayList<>();
                } else {
                    subtaskOfEpic = epic.getSubtasksOfEpic();
                }
                if (!subtaskOfEpic.contains(subtask.getId())) {
                    subtaskOfEpic.add(subtask.getId());
                }
                epic.setSubtasksOfEpic(subtaskOfEpic);
                checkStatusOfEpic(epic);
                checkTimeOfEpic(epic);
            } catch (NullPointerException e) {
                throw new TaskManagerException("Эпик с заданным ID не был добавлен");
            }
        }
    }

    @Override
    public List<Integer> getListOfSubtasksOfEpic(int id) {
        try {
            return epics.get(id).getSubtasksOfEpic();
        } catch (NullPointerException e) {
            throw new TaskManagerException("Неверный ID");
        }
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
                for (Epic epic : epics.values()) {
                    if (epic.getSubtasksOfEpic().contains(id)) {
                        List<Integer> subtasksOfEpic = getListOfSubtasksOfEpic(epic.getId());
                        subtasksOfEpic.remove(id);
                        epic.setSubtasksOfEpic(subtasksOfEpic);
                    }
                }
            }
            subtasks.clear();
        }
    }

    @Override
    public void updateTask(Task task, int id) {
        if (task != null) {
            if (tasks.containsKey(id)) {
                task.setId(id);
                tasks.put(id, task);
            } else {
                addTask(task);
            }
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
                checkTimeOfEpic(epic);
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
                checkTimeOfEpic(epics.get(subtask.getIdOfEpic()));
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
            checkTimeOfEpic(epic);
        }
    }

    @Override
    public void checkStatusOfEpic(Epic epic) {
        if (epic != null) {
            List<Integer> subtaskList = epic.getSubtasksOfEpic();
            int newSubtask = 0;
            int inProgressSubtask = 0;
            int doneSubtask = 0;
            if (!subtaskList.isEmpty()) {
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
    public void checkTimeOfEpic(Epic epic) {
        if (epic != null) {
            List<Integer> subtasksOfEpic = epic.getSubtasksOfEpic();
            LocalDateTime earliestTime = LocalDateTime.MAX;
            int duration = 0;
            for (Integer id : subtasksOfEpic) {
                Subtask subtask = subtasks.get(id);
                LocalDateTime startTime = subtask.getStartTime();
                if (startTime.isBefore(earliestTime)) {
                    earliestTime = startTime;
                }
                duration += subtask.getDuration();
            }
            epic.setStartTime(earliestTime);
            epic.setDuration(duration);
        }
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskManagerException("Неверный ID");
        } else {
            inMemoryHistoryManager.addTask(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new TaskManagerException("Неверный ID");
        } else {
            inMemoryHistoryManager.addTask(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new TaskManagerException("Неверный ID");
        } else {
            inMemoryHistoryManager.addTask(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        if (tasks.containsKey(id)) {
            inMemoryHistoryManager.removeTask(id);
            tasks.remove(id);
        } else {
            throw new TaskManagerException("Задача с таким ID не существует");
        }
    }

    @Override
    public void deleteEpicByID(int id) {
        if (epics.containsKey(id)) {
            List<Integer> subtasksOfEpic = epics.get(id).getSubtasksOfEpic();
            for (Integer idSubtask : subtasksOfEpic) {
                inMemoryHistoryManager.removeTask(idSubtask);
                subtasks.remove(idSubtask);
            }
            inMemoryHistoryManager.removeTask(id);
            epics.remove(id);
        } else {
            throw new TaskManagerException("Эпик с таким ID не существует");
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            inMemoryHistoryManager.removeTask(id);
            subtasks.remove(id);
            List<Integer> subtasksOfEpic = getListOfSubtasksOfEpic(subtask.getIdOfEpic());
            subtasksOfEpic.removeIf(idOfSubtask -> id == idOfSubtask);
            epics.get(subtask.getIdOfEpic()).setSubtasksOfEpic(subtasksOfEpic);
            checkStatusOfEpic(epics.get(subtask.getIdOfEpic()));
            checkTimeOfEpic(epics.get(subtask.getIdOfEpic()));
        } else {
            throw new TaskManagerException("Подзадачи с таким ID не существует");
        }
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
