package ru.yandex.taskTracker.Exceptions;

public class CreateServerException extends RuntimeException {

        public CreateServerException(String message, Throwable error) {
            super(message, error);
        }

        public CreateServerException (Throwable error) {
            super("Не удалось создать сервер", error);
        }

        public CreateServerException() {
            super("Не удалось создать сервер");
        }
    }

