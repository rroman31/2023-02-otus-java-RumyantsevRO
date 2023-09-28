package ru.otus.exception;

public class ExceptionOnEvenSecond extends RuntimeException {

    public ExceptionOnEvenSecond(int eventSecond) {
        super("Event processing second is: " + eventSecond);
    }

}
