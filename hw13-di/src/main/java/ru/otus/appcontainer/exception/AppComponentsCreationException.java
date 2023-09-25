package ru.otus.appcontainer.exception;

public class AppComponentsCreationException extends RuntimeException {
    public AppComponentsCreationException() {
        super();
    }

    public AppComponentsCreationException(String message) {
        super(message);
    }

    public AppComponentsCreationException(Throwable cause) {
        super(cause);
    }

    public AppComponentsCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
