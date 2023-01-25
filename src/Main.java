import java.util.Objects;
import ru.yandex.taskTracker.model.Epic;
import ru.yandex.taskTracker.model.Subtask;
import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.service.Manager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        Manager manager = new Manager();
        Task task1 = new Task("Задача 1", "Описание задачи 1", "NEW");
        Task task2 = new Task("Задача 2", "Описание задачи 2", "NEW");
        Epic epic1 = new Epic("Эпик 3", "Описание задачи 3", "NEW");
        Subtask subtask1 = new Subtask("Подтаск 4", "Описание подтаска 4", "NEW", 3);
        Subtask subtask2 = new Subtask("Подтаск 5", "Описание подтаска 5", "NEW", 3);
        Epic epic2 = new Epic("Эпик 6", "Описание задачи 6", "NEW");
        Subtask subtask3 = new Subtask("Подтаск 7", "Описание подтаска 7", "NEW", 6);
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        Objects.requireNonNull(manager);
        manager.changeStatusOfTask(task1, "DONE");
        Objects.requireNonNull(manager);
        manager.changeStatusOfTask(task2, "IN_PROGRESS");
        Objects.requireNonNull(manager);
        manager.changeStatusOfSubtask(subtask1, "IN_PROGRESS");
        Objects.requireNonNull(manager);
        manager.changeStatusOfSubtask(subtask2, "DONE");
        Objects.requireNonNull(manager);
        manager.changeStatusOfSubtask(subtask3, "IN_PROGRESS");
        manager.checkStatusOfEpic(epic1);
        manager.checkStatusOfEpic(epic2);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
        manager.deleteTaskByID(1);
        manager.deleteEpicByID(3);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}

