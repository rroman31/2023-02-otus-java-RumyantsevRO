package ru.otus.model;

import java.util.Arrays;

public enum Nominal {
    ONE_HUNDRED(100),
    FIVE_HUNDRED(500),
    THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000);

    private final int value;

    Nominal(int value) {
        this.value = value;
    }

    public static Nominal of(int value) {
        return Arrays.stream(Nominal.values())
                .filter(nominal -> nominal.value == value)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Нет подходящего номинала: " + value));
    }

    public int getValue() {
        return value;
    }
}