package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IoC {
    private IoC() {
    }

    static TestLoggingInterface createMyClass() {
        InvocationHandler handler = new LogInvocationHandler(new TestLoggingImpl());
        return (TestLoggingInterface) Proxy.newProxyInstance(IoC.class.getClassLoader(), new Class<?>[]{TestLoggingInterface.class}, handler);
    }

    private static class LogInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface myClass;
        private final List<String> list = new ArrayList<>();

        LogInvocationHandler(TestLoggingInterface myClass) {
            this.myClass = myClass;
            addLogAnnotation();
        }

        private void addLogAnnotation() {
            Method[] methods = myClass.getClass().getMethods();
            for (var method : methods) {
                if (method.isAnnotationPresent(Log.class)) {
                    list.add(method.getName() + Arrays.toString(method.getParameterTypes()));
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (list.contains(method.getName() + Arrays.toString(method.getParameterTypes()))) {
                System.out.printf("Executed method: %s, params: %s%n", method.getName(), Arrays.toString(args));
            }
            return method.invoke(myClass, args);
        }
    }
}
