package ru.yandex.taskTracker.takManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private InMemoryTaskManager taskManager;
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setManager() {
        taskManager = new InMemoryTaskManager();
        taskManager.inMemoryHistoryManager.clearHistory();
        epic = new Epic("EpicName", "EpicDescription", Status.NEW, TaskType.EPIC);
        subtask1 = new Subtask("Subtask1Name", "Subtask1Description", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 0));
        subtask2 = new Subtask("Subtask2Name", "Subtask2Description", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 15));
    }

    @Test
    void shouldNotThrowWhenEpicNull() {
        assertDoesNotThrow(() -> taskManager.checkStatusOfEpic(null));
    }

    @Test
    void shouldSetStatusNewWhenAllSubtasksNew() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.checkStatusOfEpic(epic);
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус");
    }

    @Test
    void shouldSetStatusDoneWhenAllSubtasksDone() {
        taskManager.addEpic(epic);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.checkStatusOfEpic(epic);
        assertEquals(Status.DONE, epic.getStatus(), "Неверный статус");
    }

    @Test
    void shouldSetStatusInProgressWhenAllSubtasksInProgress() {
        taskManager.addEpic(epic);
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.checkStatusOfEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус");
    }

    @Test
    void shouldSetStatusInProgressWhenSubtaskNewAndSubtaskDone() {
        taskManager.addEpic(epic);
        subtask1.setStatus(Status.DONE);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.checkStatusOfEpic(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Неверный статус");
    }

    @Test
    void shouldSetStatusNewWhenNoSubtasks() {
        taskManager.addEpic(epic);
        taskManager.checkStatusOfEpic(epic);
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус");
    }

    @Test
    void shouldNotThrowExceptionWhenEpicNull() {
        assertDoesNotThrow(() -> taskManager.checkTimeOfEpic(null));
    }

    @Test
    void shouldSetTimeMaxWhenNoSubtasks() {
        taskManager.checkTimeOfEpic(epic);
        assertNotNull(epic.getStartTime(), "Время не установлено");
        assertEquals(LocalDateTime.MAX, epic.getStartTime(), "Время установлено неверно");
    }

    @Test
    void shouldSetTimeOfSubtaskWhenOneSubtask() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask1);
        taskManager.checkTimeOfEpic(epic);
        assertNotNull(epic.getStartTime(), "Время не установлено");
        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время установлено неверно");
    }

    @Test
    void shouldSetTimeOfSubtaskWhenTwoSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.checkTimeOfEpic(epic);
        assertNotNull(epic.getStartTime(), "Время не установлено");
        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Время установлено неверно");
    }
}
