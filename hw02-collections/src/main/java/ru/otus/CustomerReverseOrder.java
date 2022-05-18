package ru.otus;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {
    private final Deque<Customer> deque = new ArrayDeque<>();

    public void add(Customer customer) {
        deque.push(customer);
    }

    public Customer take() {
        return deque.pop();
    }
}
