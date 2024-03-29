package ru.yandex.taskTracker.taskManager;

import ru.yandex.taskTracker.Exceptions.CreateFileException;
import ru.yandex.taskTracker.Exceptions.FailedReadingFileException;
import ru.yandex.taskTracker.Exceptions.FailedWritingFileException;
import ru.yandex.taskTracker.historyManager.HistoryManager;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    public static final String PATH = "src\\resources\\kanban.csv";
    private final File kanban;

    public FileBackedTasksManager(File kanban) {
        super();
        this.kanban = kanban;
    }

    static String historyToString(HistoryManager manager) {
        return manager.toString();
    }

    public static List<Integer> historyFromString(String value, FileBackedTasksManager manager) {
        List<Integer> history = new LinkedList<>();
        manager.inMemoryHistoryManager.clearHistory();
        String[] parts = value.split(",");
        if (parts.length > 1) {
            for (String part : parts) {
                if (manager.getTasks().containsKey(Integer.parseInt(part))) {
                    Task task = manager.getTaskByID(Integer.parseInt(part));
                    manager.inMemoryHistoryManager.addTask(task);
                    history.add(task.getId());
                } else if (manager.getEpics().containsKey(Integer.parseInt(part))) {
                    Epic epic = manager.getEpicByID(Integer.parseInt(part));
                    manager.inMemoryHistoryManager.addTask(epic);
                    history.add(epic.getId());
                } else if (manager.getSubtasks().containsKey(Integer.parseInt(part))) {
                    Subtask subtask = manager.getSubtaskByID(Integer.parseInt(part));
                    manager.inMemoryHistoryManager.addTask(subtask);
                    history.add(subtask.getId());
                }
            }
        }
        return history;
    }

    public static Task fromString(String value, FileBackedTasksManager manager) {
        try {
            String[] parts = value.split(","); //id, type, name, status, description, duration, startTime, epic
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1]);

            switch (type) {
                case TASK:
                    Task task = new Task(
                            parts[2],
                            parts[4],
                            Status.valueOf(parts[3]),
                            TaskType.valueOf(parts[1]),
                            Integer.parseInt(parts[5]),
                            LocalDateTime.parse(parts[6])
                    );
                    task.setId(id);
                    manager.addTaskExisted(task);
                    return task;
                case EPIC:
                    Epic epic = new Epic(
                            parts[2],
                            parts[4],
                            Status.valueOf(parts[3]),
                            TaskType.valueOf(parts[1])
                    );
                    epic.setId(id);
                    manager.addEpicExisted(epic);
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(
                            parts[2],
                            parts[4],
                            Status.valueOf(parts[3]),
                            Integer.parseInt(parts[7]),
                            TaskType.valueOf(parts[1]),
                            Integer.parseInt(parts[5]),
                            LocalDateTime.parse(parts[6])
                    );
                    subtask.setId(id);
                    manager.addSubtaskExisted(subtask);
                    return subtask;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager loadedManager = new FileBackedTasksManager(file);
        try {
            String content = Files.readString(Path.of(loadedManager.kanban.getAbsolutePath()));
            String[] lines = content.split("\r?\n");
            for (int i = 1; i < lines.length - 2; i++) {
                String line = lines[i];
                fromString(line, loadedManager);
            }
            for (Subtask subtask : loadedManager.getSubtasks().values()) {
                loadedManager.updateEpicBySubtask(subtask);
            }
            loadedManager.prioritizeAllTasks();
            if (lines.length > 2) {
                List<Integer> history = historyFromString(lines[lines.length - 1], loadedManager);
            }
            return loadedManager;

        } catch (IOException e) {
            throw new FailedReadingFileException(e);
        }
    }

    public File getKanban() {
        return kanban;
    }

    void checkFile(String path) {
        if (!Path.of(path).toFile().exists()) {
            System.out.println("jj");
            try {
                Files.createFile(Path.of(path));
            } catch (IOException e) {
                throw new CreateFileException(e);
            }
        }
    }

    void save() {
        checkFile(PATH);
        try (PrintWriter writer = new PrintWriter(new FileWriter(kanban, StandardCharsets.UTF_8))) {
            writer.println("id,type,name,status,description,epic");
            for (Task task : getTasks().values()) {
                writer.println(task.toString());
            }
            for (Epic epic : getEpics().values()) {
                writer.println(epic.toString());
            }
            for (Subtask subtask : getSubtasks().values()) {
                writer.println(subtask.toString());
            }

            writer.println("\r\n" + historyToString(super.inMemoryHistoryManager));
        } catch (IOException e) {
            throw new FailedWritingFileException(e);
        }
    }

    public void prioritizeAllTasks() {
        List<Task> temp = new ArrayList<>();
        for (Task task : super.tasks.values()) {
            if (task.getStartTime() == null) {
                temp.add(task);
            } else {
                super.prioritizedTasks.add(task);
            }
        }
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStartTime() == null) {
                temp.add(subtask);
            } else {
                prioritizedTasks.add(subtask);
            }
        }
        if (!temp.isEmpty()) {
            LocalDateTime tempStartTime = prioritizedTasks.last().getEndTime().plusMinutes(1);
            for (Task task : temp) {
                task.setStartTime(tempStartTime);
                prioritizedTasks.add(task);
                tempStartTime = task.getEndTime().plusMinutes(1);
            }
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task, int id) {
        super.updateTask(task, id);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int id) {
        super.updateEpic(epic, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int id) {
        super.updateSubtask(subtask, id);
        save();
    }

    @Override
    public void changeStatusOfTask(Task task, Status status) {
        super.changeStatusOfTask(task, status);
        save();
    }

    @Override
    public void changeStatusOfSubtask(Subtask subtask, Status status) {
        super.changeStatusOfSubtask(subtask, status);
        save();
    }

    @Override
    public void checkStatusOfEpic(Epic epic) {
        super.checkStatusOfEpic(epic);
        save();
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = super.getSubtaskByID(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }
}

