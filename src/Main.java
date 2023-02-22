import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.service.InMemoryHistoryManager;
import ru.yandex.taskTracker.service.Managers;
import ru.yandex.taskTracker.service.TaskManager;

import static ru.yandex.taskTracker.service.Status.*;

public class Main {
    public static void main(String[] args) {
        testCode();
    }

    static void testCode() {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", NEW);
        Epic epic1 = new Epic("Эпик 3", "Описание задачи 3", NEW);
        Subtask subtask1 = new Subtask("Подтаск 4", "Описание подтаска 4", NEW, 3);
        Subtask subtask2 = new Subtask("Подтаск 5", "Описание подтаска 5", NEW, 3);
        Epic epic2 = new Epic("Эпик 6", "Описание задачи 6", NEW);
        Subtask subtask3 = new Subtask("Подтаск 7", "Описание подтаска 7", NEW, 3);
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
        System.out.println(historyManager.getTasks());

        manager.deleteTaskByID(1);
        System.out.println(historyManager.getTasks());

        manager.deleteEpicByID(3);
        System.out.println(historyManager.getTasks());
    }
}


