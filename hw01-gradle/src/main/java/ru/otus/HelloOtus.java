package ru.otus;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

public class HelloOtus {
    public static void main(String[] args) {
        System.out.println("hello world");
        List<String> names = Arrays.asList("Hello", "world", "!!!!!");
        List<String> reversed = Lists.reverse(names);
        System.out.println(reversed);
    }
}