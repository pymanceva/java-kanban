package ru.yandex.taskTracker.historyManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.taskManager.InMemoryTaskManager;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryTaskManager taskManager;
    private Task task1;
    private Epic epic2;
    private Subtask subtask3;

    @BeforeEach
    void setHistoryManager() {
        taskManager = new InMemoryTaskManager();
        taskManager.inMemoryHistoryManager.clearHistory();
        task1 = new Task("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        epic2 = new Epic("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.EPIC);
        subtask3 = new Subtask("TestTaskName", "TestTaskDescription", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 15));
    }

    @Test
    void shouldAddTaskToHistory() {
        taskManager.inMemoryHistoryManager.addTask(task1);
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertFalse(savedHistoryMap.isEmpty(), "Задачи не были добавлены в Map");
        assertNotEquals(0, savedHistory.size(), "Задачи не были добавлены в List");
        assertEquals(task1, savedHistoryMap.get(0), "Задачи не совпадают");
        assertEquals(1, savedHistory.size(), "Колчество задач не совпадает");
    }

    @Test
    void shouldDoNothingWhenNullTask() {
        taskManager.inMemoryHistoryManager.addTask(null);
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertTrue(savedHistoryMap.isEmpty(), "Задачи были добавлены в Map");
        assertEquals(0, savedHistory.size(), "Задачи были добавлены в List");
    }

    @Test
    void shouldReplaceTaskIfIDAlreadyAddedToHistory() {
        taskManager.inMemoryHistoryManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(epic2);
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(1, savedHistoryMap.size(), "Задачи продублировались");
        assertEquals(1, savedHistory.size(), "Задачи продублировались");
        assertEquals(epic2, savedHistoryMap.get(0), "Сохранилась не та задача");
    }

    @Test
    void shouldRemoveAllTasksFromHistoryWhenHistoryNotEmpty() {
        task1.setId(1);
        epic2.setId(2);
        taskManager.inMemoryHistoryManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(epic2);
        taskManager.inMemoryHistoryManager.clearHistory();
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(0, savedHistory.size(), "Задачи не были удалены из List");
        assertTrue(savedHistoryMap.isEmpty(), "Задачи не были удалены из Map");
    }

    @Test
    void shouldDoNothingWhenHistoryIsEmpty() {
        taskManager.inMemoryHistoryManager.clearHistory();
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(0, savedHistory.size(), "Задачи не были удалены из List");
        assertTrue(savedHistoryMap.isEmpty(), "Задачи не были удалены из Map");
    }

    @Test
    void shouldReturnEmptyListWhenHistoryIsClear() {
        final List<Task> savedTasks = taskManager.inMemoryHistoryManager.getTasks();
        assertTrue(savedTasks.isEmpty(), "Лист не пустой");
    }

    @Test
    void shouldReturnListOfTasksFromHistory() {
        taskManager.inMemoryHistoryManager.addTask(task1);
        final List<Task> savedTasks = taskManager.inMemoryHistoryManager.getTasks();
        assertFalse(savedTasks.isEmpty(), "Лист пустой");
        assertEquals(task1, savedTasks.get(0), "Задачи не совпадают");
    }

    @Test
    void shouldRemoveLastTaskFromMapAndList() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        taskManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(epic2);
        taskManager.inMemoryHistoryManager.addTask(subtask3);
        taskManager.inMemoryHistoryManager.removeTask(task1.getId());
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(2, savedHistory.size(), "Задачи не были удалены из List");
        assertEquals(2, savedHistoryMap.size(), "Задачи не были удалены из Map");
        assertEquals(subtask3, savedHistoryMap.get(1), "Задачи не совпадают");
        assertEquals(subtask3, savedHistory.getTail().data, "Задачи не совпадают");
    }

    @Test
    void shouldRemoveMiddleTaskFromMapAndList() {
        taskManager.addEpic(epic2);
        taskManager.addTask(task1);
        taskManager.addSubtask(subtask3);
        taskManager.inMemoryHistoryManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(epic2);
        taskManager.inMemoryHistoryManager.addTask(subtask3);
        taskManager.inMemoryHistoryManager.removeTask(task1.getId());
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(2, savedHistory.size(), "Задачи не были удалены из List");
        assertEquals(2, savedHistoryMap.size(), "Задачи не были удалены из Map");
        assertEquals(subtask3, savedHistoryMap.get(1), "Задачи не совпадают");
    }

    @Test
    void shouldRemoveFirstTaskFromMapAndList() {
        taskManager.addEpic(epic2);
        taskManager.addTask(task1);
        taskManager.addSubtask(subtask3);
        taskManager.inMemoryHistoryManager.addTask(task1);
        taskManager.inMemoryHistoryManager.addTask(epic2);
        taskManager.inMemoryHistoryManager.addTask(subtask3);
        taskManager.inMemoryHistoryManager.removeTask(task1.getId());
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(2, savedHistory.size(), "Задачи не были удалены из List");
        assertEquals(2, savedHistoryMap.size(), "Задачи не были удалены из Map");
        assertEquals(epic2, savedHistoryMap.get(0), "Задачи не совпадают");
        assertEquals(epic2, savedHistory.getHead().data, "Задачи не совпадают");
    }

    @Test
    void shouldDoNothingWhenNoSuchTaskInHistory() {
        taskManager.inMemoryHistoryManager.removeTask(task1.getId());
        final List<Task> savedHistoryMap = taskManager.inMemoryHistoryManager.getTasks();
        final CustomLinkedList<Task> savedHistory = taskManager.inMemoryHistoryManager.getHistory();
        assertEquals(0, savedHistory.size(), "Задачи не были удалены из List");
        assertTrue(savedHistoryMap.isEmpty(), "Задачи не были удалены из Map");
    }

    @Test
    void shouldReturnEmptyStringWhenHistoryIsEmpty() {
        String savedHistory = taskManager.inMemoryHistoryManager.toString();
        assertEquals("", savedHistory, "История не совпадает");
    }

    @Test
    void shouldReturn0AsStringWhenHistoryIsNotEmpty() {
        taskManager.inMemoryHistoryManager.addTask(task1);
        String savedHistory = taskManager.inMemoryHistoryManager.toString();
        assertEquals("0,", savedHistory, "История не совпадает");
    }
}