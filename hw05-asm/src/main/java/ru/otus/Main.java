package ru.otus;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface testLoggingInterface = IoC.createMyClass(new TestLoggingImpl());
        testLoggingInterface.calculation(5);
        testLoggingInterface.calculation(5, 2);
        testLoggingInterface.calculation(6, 2, "*");
    }
}
