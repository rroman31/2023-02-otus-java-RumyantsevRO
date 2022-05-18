package ru.otus;

import java.util.Comparator;

public class CustomerComparator implements Comparator<Customer> {
    @Override
    public int compare(Customer o1, Customer o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1.getScores() == o2.getScores()) {
            return 0;
        }
        return o1.getScores() > o2.getScores() ? 1 : -1;
    }
}

