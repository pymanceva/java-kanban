package ru.yandex.taskTracker.Exceptions;

public class FailedReadingFileException extends RuntimeException {

    public FailedReadingFileException(String message, Throwable error) {
        super(message, error);
    }

    public FailedReadingFileException (Throwable error) {
        super("Не удалось прочитать файл", error);
    }

    public FailedReadingFileException() {
        super("Не удалось прочитать файл");
    }
}
