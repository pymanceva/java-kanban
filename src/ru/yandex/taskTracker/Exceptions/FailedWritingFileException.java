package ru.yandex.taskTracker.Exceptions;

public class FailedWritingFileException extends RuntimeException {

    public FailedWritingFileException(String message, Throwable error) {
        super(message, error);
    }

    public FailedWritingFileException(Throwable error) {
        super("Не удалось записать файл", error);
    }

    public FailedWritingFileException() {
        super("Не удалось записать файл");
    }
}
