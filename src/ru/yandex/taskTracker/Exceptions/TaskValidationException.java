package ru.yandex.taskTracker.Exceptions;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException() {
        super("Нельзя выполнять более 1 задачи одновременно");
    }
}
