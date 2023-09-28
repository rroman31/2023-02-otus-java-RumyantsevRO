package ru.otus.exception;

public class NotEnoughMoneyException extends RuntimeException {

    public NotEnoughMoneyException() {
        super("Недостаточно денег в банкомате");
    }
}
