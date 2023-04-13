package ru.yandex.taskTracker.Exceptions;

public class SendResponseHeadersException extends RuntimeException {

    public SendResponseHeadersException(String message, Throwable error) {
        super(message, error);
    }

    public SendResponseHeadersException (Throwable error) {
        super("Ошибка отправки ResponseHeaders", error);
    }

    public SendResponseHeadersException() {
        super("Ошибка отправки ResponseHeaders");
    }
}
