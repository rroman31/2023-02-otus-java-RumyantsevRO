package ru.otus.model;

public class Banknote {
    private final int value;

    private final Nominal nominal;

    public Banknote(int value) {
        this.value = value;
        this.nominal = Nominal.of(value);
    }

    public int getValue() {
        return value;
    }

    public Nominal getNominal() {
        return nominal;
    }
}