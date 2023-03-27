package ru.yandex.taskTracker.takManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskManagerException;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    public void setTaskManager() { //создает ТаскМенеджер и задачи для работы перед каждым тестом
        taskManager = new InMemoryTaskManager();
        task1 = new Task("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        epic2 = new Epic("TestTaskName", "TestTaskDescription", Status.NEW, TaskType.EPIC);
        subtask3 = new Subtask("TestTaskName", "TestTaskDescription", Status.NEW, 1,
                TaskType.SUBTASK, 5, LocalDateTime.of(2023, 1, 1, 0, 5));
    }

    @Test
    void shouldAddSubtaskAfterTask() {
        taskManager.addPrioritizedTask(task1);
        taskManager.addPrioritizedTask(subtask3);
        assertEquals(subtask3, taskManager.getPrioritizedTasks().last(), "Задачи добавлены в неверном порядке");
    }

    @Test
    void shouldDoNothingIfNullTask() {
        taskManager.addPrioritizedTask(null);
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldAddTaskWithoutStartTimeAsLast() {
        Task taskNoStartTime = new Task("name", "description", Status.NEW, TaskType.TASK,
                3, null);
        taskManager.addPrioritizedTask(new Task("1", "2", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 2, 0, 0)));
        taskManager.addPrioritizedTask(taskNoStartTime);

        assertNotNull(taskManager.getPrioritizedTasks().first(), "Задача не найдена");
        assertEquals(taskNoStartTime, taskManager.getPrioritizedTasks().last(), "Задачи не совпадают");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Неверное количество задач");
    }

    @Test
    void shouldAddCorrectTaskExisted() {
        task1.setId(1);
        taskManager.addTaskExisted(task1);
        final Task savedTask = taskManager.getTaskByID(task1.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldNotAddNullTaskExisted() {
        taskManager.addTaskExisted(null);
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void shouldAddCorrectEpicExisted() {
        epic2.setId(1);
        taskManager.addEpicExisted(epic2);
        final Epic savedEpic = taskManager.getEpicByID(epic2.getId());
        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic2, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");
    }

    @Test
    void shouldNotAddNullEpicExisted() {
        taskManager.addEpicExisted(null);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void shouldAddCorrectSubtaskExisted() {
        taskManager.addEpic(epic2);
        subtask3.setId(2);
        taskManager.addSubtaskExisted(subtask3);
        final Subtask savedSubtask = taskManager.getSubtaskByID(subtask3.getId());
        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask3, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldNotAddNullSubtaskExisted() {
        taskManager.addSubtaskExisted(null);
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldReturnTrueIfPrioritizedTasksIsEmpty() {
        boolean result = taskManager.validateTask(task1);
        assertTrue(result, "Валидация проходит некорректно");
    }

    @Test
    void shouldReturnTrueIfNewTaskBeforeAllPrevious() {
        taskManager.addPrioritizedTask(task1);
        final Task validTask = new Task("1", "2", Status.NEW, TaskType.TASK,
                15, LocalDateTime.of(2022, 1, 1, 0, 0));
        boolean result = taskManager.validateTask(validTask);
        assertTrue(result, "Валидация проходит некорректно");
    }

    @Test
    void shouldReturnTrueIfNewTaskAfterAllPrevious() {
        taskManager.addPrioritizedTask(task1);
        final Task validTask = new Task("1", "2", Status.NEW, TaskType.TASK,
                15, LocalDateTime.of(2024, 1, 1, 0, 0));
        boolean result = taskManager.validateTask(validTask);
        assertTrue(result, "Валидация проходит некорректно");
    }

    @Test
    void shouldReturnTrueIfNewTaskBetweenTwoPrevious() {
        taskManager.addPrioritizedTask(task1);
        taskManager.addPrioritizedTask(new Task("1", "2", Status.NEW, TaskType.TASK,
                15, LocalDateTime.of(2024, 1, 1, 0, 0)));
        final Task validTask = new Task("1", "2", Status.NEW, TaskType.TASK,
                15, LocalDateTime.of(2023, 2, 1, 0, 0));
        boolean result = taskManager.validateTask(validTask);
        assertTrue(result, "Валидация проходит некорректно");
    }

    @Test
    void shouldReturnFalseIfNewTaskCrossTimeWithExisted() {
        taskManager.addPrioritizedTask(task1);
        final Task invalidTask = new Task("1", "2", Status.NEW, TaskType.TASK,
                15, LocalDateTime.of(2023, 1, 1, 0, 1));
        final boolean result = taskManager.validateTask(invalidTask);
        assertFalse(result, "Валидация проходит некорректно");
    }

    @Test
    void shouldReturnEmptyListWhenEpicHasNoSubtasks() {
        taskManager.addEpic(epic2);
        final List<Integer> savedSubTasks = taskManager.getListOfSubtasksOfEpic(1);
        assertNotNull(savedSubTasks, "Список не вернулся");
        assertTrue(savedSubTasks.isEmpty(), "Список не пуст");
    }

    @Test
    void shouldReturnListOfSubtasksWhenEpicHasSubtasks() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final List<Integer> savedSubTasks = taskManager.getListOfSubtasksOfEpic(1);
        assertNotNull(savedSubTasks, "Список не вернулся");
        assertFalse(savedSubTasks.isEmpty(), "Список пуст");
        assertEquals(subtask3.getId(), savedSubTasks.get(0), "Отрадена не верная подзадача");
    }

    @Test
    void shouldThrowExceptionWhenID5() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () ->
                taskManager.getListOfSubtasksOfEpic(5));
        assertEquals("Неверный ID", e.getMessage());
    }

    @Test
    void shouldAddSubtaskToEpicAndUpdateTimeOfEpicWhenSubtaskIsInList() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        taskManager.updateEpicBySubtask(subtask3);
        final List<Integer> subtasksOfEpic = taskManager.getListOfSubtasksOfEpic(epic2.getId());
        assertEquals(2, subtasksOfEpic.get(0), "Субтаск отсутствует в списке");
        assertEquals(subtask3.getStartTime(), epic2.getStartTime(), "Неверное время начала эпика");
        assertEquals(subtask3.getDuration(), epic2.getDuration(), "Неверная продолжительность эпика");
    }

    @Test
    void shouldLeaveEmptyListWhenSubtaskNull() {
        taskManager.addEpic(epic2);
        taskManager.updateEpicBySubtask(null);
        final List<Integer> subtasksOfEpic = taskManager.getListOfSubtasksOfEpic(epic2.getId());
        assertEquals(0, subtasksOfEpic.size(), "Размер списка несоответсвует");
    }

    @Test
    void shouldThrowExceptionWhenEpicNull() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () ->
                taskManager.updateEpicBySubtask(subtask3));
        assertEquals("Эпик с заданным ID не был добавлен", e.getMessage());
    }
}
