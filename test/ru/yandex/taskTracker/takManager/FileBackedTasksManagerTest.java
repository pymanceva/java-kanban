package ru.yandex.taskTracker.takManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskManagerException;
import ru.yandex.taskTracker.util.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    public static final String PATH = "src\\resources\\kanbanTest.csv";

    @BeforeEach
    @Override
    public void setTaskManager() { //создает ТаскМенеджер и задачи для работы перед каждым тестом
        taskManager = new FileBackedTasksManager(Path.of(PATH).toFile());
        taskManager.inMemoryHistoryManager.clearHistory();
        task1 = new Task("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        epic2 = new Epic("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.EPIC);
        subtask3 = new Subtask("TestTaskName", "TestTaskDescription", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 10));
    }

    @Test
    void shouldSetHistoryFromString() {
        taskManager.addEpic(epic2);
        taskManager.addTask(task1);
        taskManager.addSubtask(subtask3);
        List<Integer> history = FileBackedTasksManager.historyFromString("1,2,3", taskManager);
        assertEquals(1, history.get(0), "История считана не верно");
        assertEquals(2, history.get(1), "История считана не верно");
        assertEquals(3, history.get(2), "История считана не верно");

    }

    @Test
    void shouldSetEmptyHistoryWhenNoHistoryInFile() {
        List<Integer> history = FileBackedTasksManager.historyFromString("", taskManager);
        assertTrue(history.isEmpty(), "История считана не верно");
    }

    @Test
    void shouldReturnTaskFromString() {
        Task loadedTask = FileBackedTasksManager.fromString(
                "0,TASK,TestTaskName,NEW,TestTaskDescription,5,2023-01-01T00:00", taskManager);
        assertEquals(task1, loadedTask, "Задача считана не верно");
    }

    @Test
    void shouldReturnEpicFromString() {
        Task loadedEpic = FileBackedTasksManager.fromString("0,EPIC,TestTaskName,NEW,TestTaskDescription",
                taskManager);
        assertEquals(epic2, loadedEpic, "Задача считана не верно");
    }

    @Test
    void shouldReturnSubtaskFromString() {
        Task loadedSubtask = FileBackedTasksManager.fromString(
                "0,SUBTASK,TestTaskName,NEW,TestTaskDescription,5,2023-01-01T00:10,1", taskManager);
        assertEquals(subtask3, loadedSubtask, "Задача считана не верно");
    }

    @Test
    void shouldReturnNullIfEmptyString() {
        Task loadedSubtask = FileBackedTasksManager.fromString("", taskManager);
        assertNull(loadedSubtask, "Ошибка");
    }

    @Test
    void shouldCreateFileWhenNoFile() {
        taskManager.checkFile(PATH);
        File setFile = taskManager.getKanban();
        assertNotNull(setFile, "Файл не был создан");
    }

    @Test
    void shouldSave1lineIfListOfTasksIsEmpty() throws IOException {
        taskManager.save();
        String result = Files.readString(Path.of(PATH));
        assertEquals("id,type,name,status,description,epic\r\n\r\n\r\n", result,
                "Сохранение в файл некорректно");
    }

    @Test
    void shouldSaveTaskToFile() throws IOException {
        taskManager.addTask(task1);
        String result = Files.readString(Path.of(PATH));
        assertEquals("id,type,name,status,description,epic\r\n1,TASK,TestTaskName,NEW,TestTaskDescription,5,2023-01-01T00:00\r\n\r\n\r\n",
                result, "Сохранение в файл некорректно");
    }

    @Test
    void shouldSaveTaskAndHistoryToFile() throws IOException {
        taskManager.addTask(task1);
        taskManager.getTaskByID(1);
        String result = Files.readString(Path.of(PATH));
        assertEquals("id,type,name,status,description,epic\r\n1,TASK,TestTaskName,NEW,TestTaskDescription,5,2023-01-01T00:00\r\n\r\n1,\r\n",
                result, "Сохранение в файл некорректно");
    }

    @Test
    void shouldPrioritizeAllTasksWhenTasksExist() {
        taskManager.addTaskExisted(task1);
        taskManager.addSubtaskExisted(subtask3);
        taskManager.prioritizeAllTasks();
        final TreeSet<Task> savedPrioritizedTasks = taskManager.getPrioritizedTasks();
        assertFalse(savedPrioritizedTasks.isEmpty(), "Задачи не были добавлены в приоритетный список");
        assertEquals(task1, savedPrioritizedTasks.first(), "Задачи неверно приоритезированы");
    }

    @Test
    void shouldReturnEmptySetWhenTasksMapIsEmpty() {
        taskManager.prioritizeAllTasks();
        final TreeSet<Task> savedPrioritizedTasks = taskManager.getPrioritizedTasks();
        assertTrue(savedPrioritizedTasks.isEmpty(), "Список не пуст");
    }

    @Test
    void shouldPrioritizeAsLastWhenNoTime() {
        taskManager.addTaskExisted(task1);
        taskManager.addSubtaskExisted(subtask3);
        Task taskNoTime = new Task("1", "2", Status.NEW, TaskType.TASK, 5, null);
        taskManager.addTaskExisted(taskNoTime);
        taskManager.prioritizeAllTasks();
        final TreeSet<Task> savedPrioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(taskNoTime, savedPrioritizedTasks.last(), "Задачи неверно приоритезированы");
    }

    @Test
    void shouldReturnLoadedManagerFromFile() {
        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(Path.of(PATH).toFile());
        assertNotNull(loadedManager, "Менеджер из файла не создан");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenWrongFile() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () ->
                FileBackedTasksManager.loadFromFile(new File("dc")));
        assertEquals("Произошла ошибка при чтении данных из файла", e.getMessage());
    }
}