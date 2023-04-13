package ru.yandex.taskTracker.taskManager;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {


    @Test
    void getFileBackedTaskManager() {
        TaskManager manager = Managers.getFileBackedTaskManager(new File("ex"));
        assertNotNull(manager, "Менеджер не был создан");
        assertEquals(manager.getClass(), FileBackedTasksManager.class, "Неверно создан менеджер");
    }
}