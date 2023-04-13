import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.server.HttpTaskServer;
import ru.yandex.taskTracker.server.KVServer;
import ru.yandex.taskTracker.taskManager.HttpTaskManager;
import ru.yandex.taskTracker.taskManager.Managers;
import ru.yandex.taskTracker.taskManager.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

import static ru.yandex.taskTracker.util.Status.NEW;
import static ru.yandex.taskTracker.util.TaskType.*;

public class Main {
    public static void main(String[] args) throws IOException {
        testCode();
    }

    static void testCode() throws IOException {
        System.out.println("Поехали!");

        HttpTaskServer server = new HttpTaskServer();
        server.start();


        KVServer kvServer;
        TaskManager manager = null;
        try {
            kvServer = new KVServer();
            kvServer.start();
            manager = Managers.getDefault();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Task task1 = new Task("task 1", "описание 1", NEW, TASK, 15,
                LocalDateTime.of(2023, 3, 22, 8, 0));

        Epic epic1 = new Epic("epic 2", "описание 3", NEW, EPIC);

        Subtask subtask1 = new Subtask("subtask 3", "описание 4", NEW, 2, SUBTASK, 15,
                LocalDateTime.of(2023, 3, 22, 7, 0));

        Subtask subtask2 = new Subtask("subtask 4", "описание 5", NEW, 2, SUBTASK, 15,
                LocalDateTime.of(2023, 3, 22, 2, 0));

        Task task2 = new Task("task 2", "описание 2", NEW, TASK, 15,
                LocalDateTime.of(2023, 3, 23, 8, 0));

        manager.addTask(task1);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addTask(task2);
        manager.getTaskByID(1);
        manager.getSubtaskByID(3);
        manager.getEpicByID(2);

        HttpTaskManager manager1 = null;
        try {
            manager1 = HttpTaskManager.loadFromServer();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(manager.getTasks());
        System.out.println(manager1.getTasks());

    }
}


