package ru.yandex.taskTracker.Exceptions;

public class FailedSaveOnServerException extends RuntimeException {

    public FailedSaveOnServerException(String message, Throwable error) {
        super(message, error);
    }

    public FailedSaveOnServerException(Throwable error) {
        super("Не удалось сохранить данные на сервере", error);
    }

    public FailedSaveOnServerException() {
        super("Не удалось сохранить данные на сервере");
    }
}
