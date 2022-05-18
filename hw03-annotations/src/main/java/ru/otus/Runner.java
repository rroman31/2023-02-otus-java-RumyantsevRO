package ru.otus;

import ru.otus.processor.TestProcessor;

public class Runner {
    public static void main(String[] args) {
        TestProcessor testProcessor = new TestProcessor();
        testProcessor.run(TestClass.class, AnotherTestClass.class);
    }
}