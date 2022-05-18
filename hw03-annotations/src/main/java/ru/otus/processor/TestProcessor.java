package ru.otus.processor;

import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;
import ru.otus.model.AnnotationsNames;
import ru.otus.model.TestMethodResult;
import ru.otus.model.TestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.otus.model.AnnotationsNames.*;

public class TestProcessor {
    private final String PASSED = "PASSED";
    private final String FAILED = "FAILED";

    public final void run(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            Map<AnnotationsNames, List<Method>> testClassProcessingResult = processingTestClass(clazz);
            TestResult testResult = runTestCycle(testClassProcessingResult, clazz);
            printResult(testResult, clazz);
        }
    }

    private Map<AnnotationsNames, List<Method>> processingTestClass(Class<?> clazz) {
        Map<AnnotationsNames, List<Method>> testClassProcessingResult = new HashMap<>();
        for (Method method : clazz.getMethods()) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length > 1) {
                throw new TestException("Method marked more than one annotation");
            }
            processingMethodAnnotations(annotations, method, testClassProcessingResult);
        }
        return testClassProcessingResult;
    }

    private void processingMethodAnnotations(Annotation[] annotations, Method method, Map<AnnotationsNames, List<Method>> testClassProcessingResult) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof After) {
                testClassProcessingResult.computeIfAbsent(AFTER, k -> new ArrayList<>()).add(method);
            }
            if (annotation instanceof Before) {
                testClassProcessingResult.computeIfAbsent(BEFORE, k -> new ArrayList<>()).add(method);
            }
            if (annotation instanceof Test) {
                testClassProcessingResult.computeIfAbsent(TEST, k -> new ArrayList<>()).add(method);
            }
        }
    }

    private <T> TestResult runTestCycle(Map<AnnotationsNames, List<Method>> testClassProcessingResult, Class<?> clazz) {
        TestResult testResult = new TestResult();
        for (Method testMethod : testClassProcessingResult.get(TEST)) {
            Object object = instantiateObjectAndThrowExceptionIfNeed(clazz);

            for (Method method : testClassProcessingResult.get(BEFORE)) {
                invokeAndThrowExceptionIfNeed(method, object);
            }

            try {
                testMethod.invoke(object);
                testResult.incrementPassedCount();
                testResult.addMethodResult(new TestMethodResult(testMethod, true, null));
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    testResult.incrementFailCount();
                    testResult.addMethodResult(new TestMethodResult(testMethod, false, ((InvocationTargetException) e).getTargetException()));
                } else {
                    testResult.incrementFailCount();
                    testResult.addMethodResult(new TestMethodResult(testMethod, false, e));
                }
            }

            for (Method method : testClassProcessingResult.get(AFTER)) {
                invokeAndThrowExceptionIfNeed(method, object);
            }
        }
        return testResult;
    }

    private void invokeAndThrowExceptionIfNeed(Method method, Object object) {
        try {
            method.invoke(object);
        } catch (Exception e) {
            throw new TestException("Method invoke error", e);
        }
    }

    private Object instantiateObjectAndThrowExceptionIfNeed(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new TestException("Object creation error", e);
        }
    }

    private void printResult(TestResult testResult, Class<?> clazz) {
        String headerTemplate = " %n Class name: %s; Tests count: %d; Test passed: %d; Tests failed: %d  %n";
        System.out.printf((headerTemplate) + "%n", clazz.getName(), testResult.getTestMethodResults().size(), testResult.getPassedCount(), testResult.getFailCount());

        String template = "Method name: %s ; result: %s";
        String errorTemplate = "Method error: %s";

        for (TestMethodResult testMethodResult : testResult.getTestMethodResults()) {
            System.out.printf((template) + "%n", testMethodResult.getMethod().getName(), testMethodResult.getPassed() ? PASSED : FAILED);
            if (!testMethodResult.getPassed()) {
                System.out.printf((errorTemplate) + "%n", testMethodResult.getException().getMessage());
            }
        }
    }
}