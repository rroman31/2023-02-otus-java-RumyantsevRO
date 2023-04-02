
package ru.otus;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class AnotherTestClass {

    @Before
    public void setUp() {
    }

    @Before
    public void setUp1() {
    }

    @Before
    public void setUp2() {
    }

    @Test
    public void testMethod() {
    }

    @Test
    public void testMethod1() {
    }

    @Test
    public void testMethodWithException() {
        throw new AssertionError("assertion error test");
    }

    @Test
    public void testMethodWithException1() {
        throw new AssertionError("assertion error test");
    }

    @After
    public void tearDown1() {
    }

    @After
    public void tearDown2() {
    }

    @After
    public void tearDown3() {

    }
}