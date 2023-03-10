import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.takManager.FileBackedTasksManager;
import ru.yandex.taskTracker.takManager.TaskManager;

import java.nio.file.Path;

import static ru.yandex.taskTracker.util.Status.*;
import static ru.yandex.taskTracker.util.TaskType.*;

public class Main {
    public static void main(String[] args) {
        testCode();
    }

    static void testCode() {
        System.out.println("Поехали!");
        TaskManager manager = new FileBackedTasksManager(Path.of(FileBackedTasksManager.path).toFile());
        Task task1 = new Task("task 1", "лалала 1", NEW, TASK);
        Task task2 = new Task("task 2", "lalala2", NEW, TASK);
        Epic epic1 = new Epic("task 3", "jjj", NEW, EPIC);
        Subtask subtask1 = new Subtask("subtask 4", "uuu 4", NEW, 3, SUBTASK);
        Subtask subtask2 = new Subtask("subtask 5", "hhh 5", NEW, 3, SUBTASK);
        Epic epic2 = new Epic("Epic 6", "iii 6", NEW, EPIC);
        Subtask subtask3 = new Subtask("Subtask 7", "lll 7", NEW, 3, SUBTASK);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);

        manager.getTaskByID(1);
        manager.getSubtaskByID(4);
        manager.getEpicByID(3);
        manager.getTaskByID(2);
        manager.getSubtaskByID(7);
        manager.getSubtaskByID(5);
        manager.getTaskByID(2);
        manager.getSubtaskByID(7);
        manager.getTaskByID(88);

        FileBackedTasksManager manager1 = FileBackedTasksManager.loadFromFile(Path.of(FileBackedTasksManager.path).toFile());
        System.out.println(manager1.getTasks());
        System.out.println(manager1.getEpics());
        System.out.println(manager1.getSubtasks());
        System.out.println(manager1.inMemoryHistoryManager.toString());


    }
}


