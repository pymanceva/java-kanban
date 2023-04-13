package ru.yandex.taskTracker.Exceptions;

public class IncorrectIDException extends RuntimeException{

    public IncorrectIDException(String message) {
        super(message);
    }

    public IncorrectIDException() {
        super("Неверный ID");
    }
}
