
package ru.otus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestResult {
    private Integer failCount;
    private Integer passedCount;
    private final List<TestMethodResult> testMethodResults;

    public TestResult() {
        this.failCount = 0;
        this.passedCount = 0;
        this.testMethodResults = new ArrayList<>();
    }

    public Integer getFailCount() {
        return failCount;
    }

    public Integer getPassedCount() {
        return passedCount;
    }

    public List<TestMethodResult> getTestMethodResults() {
        return Collections.unmodifiableList(testMethodResults);
    }

    public void addMethodResult(TestMethodResult testMethodResult) {
        this.testMethodResults.add(testMethodResult);
    }

    public void incrementFailCount() {
        this.failCount++;
    }

    public void incrementPassedCount() {
        this.passedCount++;
    }
}