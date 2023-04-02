package ru.otus.model;

import java.lang.reflect.Method;

public class TestMethodResult {
    private final Method method;
    private final Boolean isPassed;
    private final Throwable exception;

    public TestMethodResult(Method method, Boolean isPassed, Throwable exception) {
        this.method = method;
        this.isPassed = isPassed;
        this.exception = exception;
    }

    public Method getMethod() {
        return method;
    }

    public Boolean getPassed() {
        return isPassed;
    }

    public Throwable getException() {
        return exception;
    }
}