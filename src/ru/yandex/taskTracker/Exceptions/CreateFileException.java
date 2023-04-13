package ru.yandex.taskTracker.Exceptions;

public class CreateFileException extends RuntimeException {

    public CreateFileException(String message, Throwable error) {
        super(message, error);
    }

    public CreateFileException(Throwable error) {
        super("Не удалось создать файл", error);
    }

    public CreateFileException() {
        super("Не удалось создать файл");
    }
}
