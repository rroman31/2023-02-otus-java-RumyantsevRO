package ru.otus.executors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var lock = new ReentrantLock();
        var condition = lock.newCondition();
        var counters = new Counters(new MutableInt(0), new MutableInt(0), new MutableBoolean(true));

        Thread thread1 = new Thread(new Action(
                lock,
                condition,
                counters,
                1,
                10
        ));
        Thread thread2 = new Thread(new Action(
                lock,
                condition,
                counters,
                2,
                10
        ));
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }

    record Counters(MutableInt counter1, MutableInt counter2, MutableBoolean increment)
    {}

    @RequiredArgsConstructor
    public static class Action implements Runnable {
        private final Lock lock;
        private final Condition condition;
        private final Counters counters;
        private final int threadNumber;
        private final int max;

        @Override
        public void run() {
            for (;;) {
                try {
                    lock.lock();

                    var selfCounter = threadNumber == 1 ? counters.counter1() : counters.counter2();
                    var foreignCounter = threadNumber == 1 ? counters.counter2() : counters.counter1();
                    var increment = counters.increment();

                    while (waitCondition(increment, selfCounter, foreignCounter)) {
                        condition.await();
                    }

                    if (selfCounter.getValue() == max) {
                        increment.setFalse();
                    }

                    if (selfCounter.getValue() < max && increment.getValue()) {
                        selfCounter.increment();
                    } else {
                        selfCounter.decrement();
                    }

                    System.out.println("Thread-" + threadNumber + " Counter:" + selfCounter.getValue());
                    TimeUnit.MILLISECONDS.sleep(500);

                    condition.signal();

                    if (selfCounter.getValue() == 0)
                        break;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        }

        private boolean waitCondition(MutableBoolean increment, MutableInt selfCounter, MutableInt foreignCounter) {
            var waitCondition = increment.getValue() ?
                    selfCounter.getValue() > foreignCounter.getValue() :
                    selfCounter.getValue() < foreignCounter.getValue();

            return waitCondition || (this.threadNumber != 1 && foreignCounter.getValue() == 0);
        }

    }

}