package ru.yandex.taskTracker.takManager;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void shouldReturnInMemoryTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер не был создан");
        assertEquals(manager.getClass(), InMemoryTaskManager.class, "Неверно создан менеджер");
    }

    @Test
    void getFileBackedTaskManager() {
        TaskManager manager = Managers.getFileBackedTaskManager(new File("ex"));
        assertNotNull(manager, "Менеджер не был создан");
        assertEquals(manager.getClass(), FileBackedTasksManager.class, "Неверно создан менеджер");
    }
}