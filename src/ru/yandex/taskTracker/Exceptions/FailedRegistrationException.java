package ru.yandex.taskTracker.Exceptions;

public class FailedRegistrationException extends RuntimeException {

    public FailedRegistrationException(String message, Throwable error) {
        super(message, error);
    }

    public FailedRegistrationException(Throwable error) {
        super("Не удалось зарегистрироваться на сервере", error);
    }

    public FailedRegistrationException() {
        super("Не удалось зарегистрироваться на сервере");
    }
}
