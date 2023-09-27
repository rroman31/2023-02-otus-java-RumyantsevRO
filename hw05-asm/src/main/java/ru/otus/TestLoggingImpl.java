package ru.otus;

public class TestLoggingImpl implements TestLoggingInterface {

    @Override
    @Log
    public void calculation(int a) {
        System.out.println("a");
    }

    @Override
    @Log
    public void calculation(int a, int b) {
        int c = a + b;
        System.out.println(a + " + " + b + " = " + c);
    }

    @Override
    @Log
    public void calculation(int a, int b, String c) {
        switch (c) {
            case "/":
                System.out.printf("%d / %d = %d", a, b, a / b);
                break;
            case "*":
                System.out.printf("%d * %d = %d", a, b, a * b);
                break;
            case "-":
                System.out.printf("%d - %d = %d", a, b, a - b);
                break;
            case "+":
                System.out.printf("%d + %d = %d", a, b, a + b);
                break;
            default:
                throw new UnsupportedOperationException("Not supported operation");
        }
    }

}
