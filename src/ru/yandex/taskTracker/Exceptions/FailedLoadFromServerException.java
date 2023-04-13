package ru.yandex.taskTracker.Exceptions;

public class FailedLoadFromServerException extends RuntimeException {

    public FailedLoadFromServerException(String message, Throwable error) {
        super(message, error);
    }

    public FailedLoadFromServerException(Throwable error) {
        super("Не удалось получить данные с сервера", error);
    }

    public FailedLoadFromServerException() {
        super("Не удалось получить данные с сервера");
    }
}
