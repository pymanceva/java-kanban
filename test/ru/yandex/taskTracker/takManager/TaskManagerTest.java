package ru.yandex.taskTracker.takManager;

import org.junit.jupiter.api.Test;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Status;
import ru.yandex.taskTracker.util.TaskManagerException;
import ru.yandex.taskTracker.util.TaskType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;

    abstract void setTaskManager();

    @Test
    public void shouldAddCorrectTask() {
        taskManager.addTask(task1);
        final Task savedTask = taskManager.getTaskByID(task1.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");

        final Map<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(task1.getId()), "Задачи не совпадают");

    }

    @Test
    void shouldNotAddNullTask() {
        taskManager.addTask(null);
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenInvalidTask() {
        taskManager.addTask(task1);
        Task invalidTask = new Task("1", "1", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.addTask(invalidTask));
        assertEquals("Нельзя выполнять более 1 задачи за раз", e.getMessage());
    }

    @Test
    void shouldAddCorrectSubtask() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final Task savedSubtask = taskManager.getSubtaskByID(subtask3.getId());
        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask3, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");

        final Map<Integer, Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество задач");
        assertEquals(subtask3, subtasks.get(subtask3.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotAddNullSubtask() {
        taskManager.addSubtask(null);
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenInvalidSubtask() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        Subtask invalidSubtask = new Subtask("1", "1", Status.NEW, 2, TaskType.SUBTASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 5));
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.addSubtask(invalidSubtask));
        assertEquals("Нельзя выполнять более 1 задачи за раз", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoEpicOfSubtask() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.addSubtask(subtask3));
        assertEquals("Эпик, которому принадлежит эта задача, не найден", e.getMessage());
    }


    @Test
    void shouldAddCorrectEpic() {
        taskManager.addEpic(epic2);
        final Task savedEpic = taskManager.getEpicByID(epic2.getId());
        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic2, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");

        final Map<Integer, Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(epic2, epics.get(epic2.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotAddNullEpic() {
        taskManager.addEpic(null);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void shouldReturnTaskID1() {
        taskManager.addTask(task1);
        final Task savedTask = taskManager.getTaskByID(1);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenTaskID2() {
        taskManager.addTask(task1);
        final TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.getTaskByID(2));
        assertEquals("Неверный ID", e.getMessage());
    }

    @Test
    void shouldReturnSubtaskID2() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final Subtask savedSubtask = taskManager.getSubtaskByID(2);

        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask3, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenSubtaskAndID3() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.getSubtaskByID(3));
        assertEquals("Неверный ID", e.getMessage());
    }

    @Test
    void shouldReturnEpicID1() {
        taskManager.addEpic(epic2);
        final Epic savedEpic = taskManager.getEpicByID(1);

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic2, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenEpicAndID2() {
        taskManager.addEpic(epic2);
        final TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.getEpicByID(2));
        assertEquals("Неверный ID", e.getMessage());
    }

    @Test
    void shouldDeleteAllTasksFromMap() {
        taskManager.addTask(task1);
        final Map<Integer, Task> taskBeforeDelete = taskManager.getTasks();
        assertFalse(taskBeforeDelete.isEmpty(), "Задачи не были добавлены");
        taskManager.deleteAllTasks();
        final Map<Integer, Task> taskAfterDelete = taskManager.getTasks();
        assertTrue(taskAfterDelete.isEmpty(), "Задачи не были удалены");
    }

    @Test
    void shouldDeleteAllEpicsFromMap() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final Map<Integer, Epic> epicBeforeDelete = taskManager.getEpics();
        assertFalse(epicBeforeDelete.isEmpty(), "Эпики не были добавлены");
        taskManager.deleteAllEpics();
        final Map<Integer, Epic> epicAfterDelete = taskManager.getEpics();
        final Map<Integer, Subtask> subtasksAfterDelete = taskManager.getSubtasks();
        assertTrue(epicAfterDelete.isEmpty(), "Эпики не были удалены");
        assertTrue(subtasksAfterDelete.isEmpty(), "Подзадачи эпиков не были удалены");
    }

    @Test
    void shouldDeleteAllSubtasksFromMap() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        final Map<Integer, Subtask> subtaskBeforeDelete = taskManager.getSubtasks();
        final List<Integer> subtasksOfEpicBeforeDelete = taskManager.getListOfSubtasksOfEpic(1);
        assertFalse(subtasksOfEpicBeforeDelete.isEmpty(), "ID субтасков не были добавлены в эпик");
        assertFalse(subtaskBeforeDelete.isEmpty(), "Задачи не были добавлены");
        taskManager.deleteAllSubtasks();
        final Map<Integer, Subtask> subtaskAfterDelete = taskManager.getSubtasks();
        final List<Integer> subtasksOfEpicAfterDelete = taskManager.getListOfSubtasksOfEpic(1);
        assertTrue(subtaskAfterDelete.isEmpty(), "Задачи не были удалены");
        assertTrue(subtasksOfEpicAfterDelete.isEmpty(), "ID субтасков не были удалены из эпика");

    }

    @Test
    void shouldChangeStatusOfTaskForDone() {
        taskManager.changeStatusOfTask(task1, Status.DONE);
        Status status = task1.getStatus();
        assertEquals(Status.DONE, status, "Статус не соответствует");
    }

    @Test
    void shouldChangeStatusOfSubtaskForDone() {
        taskManager.changeStatusOfSubtask(subtask3, Status.DONE);
        Status status = subtask3.getStatus();
        assertEquals(Status.DONE, status, "Статус не соответствует");
    }

    @Test
    void shouldReplaceWithUpdatedTaskWhenTaskWithSuchIDAlreadyAddedToMap() {
        taskManager.addTask(task1);
        Task updatedTask = new Task("1", "2", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        taskManager.updateTask(updatedTask, task1.getId());
        final Task savedTask = taskManager.getTaskByID(task1.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");

        final Map<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(updatedTask, tasks.get(task1.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotChangeExistedTasksWhenNullTask() {
        taskManager.addTask(task1);
        taskManager.updateTask(null, task1.getId());
        final Task savedTask = taskManager.getTaskByID(task1.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldAddTaskWhenNoSuchIDInTaskMap() {
        task1.setId(1);
        taskManager.updateTask(task1, 1);
        final Task savedTask = taskManager.getTaskByID(1);

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
        assertEquals(1, savedTask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldReplaceWithUpdatedEpicWhenEpicWithSuchIDAlreadyAddedToMap() {
        taskManager.addEpic(epic2);
        Epic updatedEpic = new Epic("1", "2", Status.NEW, TaskType.EPIC);
        taskManager.updateEpic(updatedEpic, epic2.getId());
        final Epic savedEpic = taskManager.getEpicByID(epic2.getId());

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(updatedEpic, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");

        final Map<Integer, Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество задач");
        assertEquals(updatedEpic, epics.get(epic2.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotChangeExistedEpicsWhenNullEpic() {
        taskManager.addEpic(epic2);
        taskManager.updateEpic(null, epic2.getId());
        final Epic savedEpic = taskManager.getEpicByID(epic2.getId());

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic2, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");
    }

    @Test
    void shouldAddEpicWhenNoSuchIDInEpicMap() {
        epic2.setId(1);
        taskManager.updateEpic(epic2, 1);
        final Epic savedEpic = taskManager.getEpicByID(1);

        assertNotNull(savedEpic, "Задача не найдена");
        assertEquals(epic2, savedEpic, "Задачи не совпадают");
        assertEquals(1, savedEpic.getId(), "Неверно задан ID");
    }

    @Test
    void shouldReplaceWithUpdatedSubtaskWhenSubtaskWithSuchIDAlreadyAddedToMap() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        Subtask updatedSubtask = new Subtask("1", "2", Status.NEW, 2, TaskType.SUBTASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 0));
        taskManager.updateSubtask(updatedSubtask, subtask3.getId());
        final Subtask savedSubtask = taskManager.getSubtaskByID(subtask3.getId());

        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(updatedSubtask, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");

        final Map<Integer, Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество задач");
        assertEquals(updatedSubtask, subtasks.get(subtask3.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotChangeExistedSubtasksWhenNullSubtask() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        taskManager.updateSubtask(null, subtask3.getId());
        final Subtask savedSubtask = taskManager.getSubtaskByID(subtask3.getId());

        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask3, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldAddSubtaskWhenNoSuchIDInSubtaskMap() {
        taskManager.addEpic(epic2);
        subtask3.setId(2);
        taskManager.updateSubtask(subtask3, 2);
        final Subtask savedSubtask = taskManager.getSubtaskByID(2);
        assertNotNull(savedSubtask, "Задача не найдена");
        assertEquals(subtask3, savedSubtask, "Задачи не совпадают");
        assertEquals(2, savedSubtask.getId(), "Неверно задан ID");
    }

    @Test
    void shouldDeleteTaskFromMapWhenIDExists() {
        taskManager.addTask(task1);
        taskManager.deleteTaskByID(1);
        final Map<Integer, Task> deleted = taskManager.getTasks();

        assertTrue(deleted.isEmpty(), "Задача не была удалена");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenID2() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.deleteTaskByID(2));
        assertEquals("Задача с таким ID не существует", e.getMessage());
    }

    @Test
    void shouldDeleteEpicFromMapWhenIDExists() {
        taskManager.addEpic(epic2);
        taskManager.deleteEpicByID(1);
        final Map<Integer, Epic> deleted = taskManager.getEpics();

        assertTrue(deleted.isEmpty(), "Задача не была удалена");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenDeleteEpicWithID2() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.deleteEpicByID(2));
        assertEquals("Эпик с таким ID не существует", e.getMessage());
    }

    @Test
    void shouldDeleteSubtaskFromMapWhenIDExists() {
        taskManager.addEpic(epic2);
        taskManager.addSubtask(new Subtask("1", "2", Status.NEW, 1, TaskType.SUBTASK,
                5, LocalDateTime.of(2023, 1, 1, 0, 30)));
        taskManager.addSubtask(subtask3);
        taskManager.deleteSubtaskByID(2);
        final Map<Integer, Subtask> deleted = taskManager.getSubtasks();

        assertEquals(1, deleted.size(), "Задача не была удалена");
    }

    @Test
    void shouldThrowTaskManagerExceptionWhenDeleteSubtaskWithID2() {
        TaskManagerException e = assertThrows(TaskManagerException.class, () -> taskManager.deleteSubtaskByID(2));
        assertEquals("Подзадачи с таким ID не существует", e.getMessage());
    }

    @Test
    void shouldAddPrioritizedTask() {
        taskManager.addPrioritizedTask(task1);
        taskManager.addPrioritizedTask(new Task("1", "2", Status.NEW, TaskType.TASK,
                5, LocalDateTime.of(2023, 1, 2, 0, 0)));

        assertNotNull(taskManager.getPrioritizedTasks().first(), "Задача не найдена");
        assertEquals(task1, taskManager.getPrioritizedTasks().first(), "Задачи не совпадают");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Неверное количество задач");
    }
}