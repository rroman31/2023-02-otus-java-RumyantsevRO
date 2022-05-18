package ru.otus;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class CustomerService {
    private final NavigableMap<Customer, String> storage = new TreeMap<>(new CustomerComparator());

    public Map.Entry<Customer, String> getSmallest() {
        return createCopy(storage.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return createCopy(storage.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        storage.put(customer, data);
    }

    private Entry createCopy(Map.Entry<Customer, String> entry) {
        return Objects.isNull(entry) ? null : new Entry(entry);
    }

    private static class Entry implements Map.Entry<Customer, String> {
        private final Customer key;
        private String value;

        public Entry(Customer key, String value) {
            this.key = key;
            this.value = value;
        }

        public Entry(Map.Entry<Customer, String> entry) {
            this.key = new Customer(entry.getKey());
            this.value = entry.getValue();
        }

        @Override
        public Customer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            this.value = value;
            return this.value;
        }
    }
}
