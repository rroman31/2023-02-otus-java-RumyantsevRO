package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unchecked"})
public class IoC {
    private IoC() {
    }

    static <T> T createMyClass(T obj) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException("Object must not be null");
        }
        Class<T> clazz = (Class<T>) obj.getClass();
        InvocationHandler handler = new LogInvocationHandler<>(obj);
        return (T) Proxy.newProxyInstance(IoC.class.getClassLoader(), clazz.getInterfaces(), handler);
    }

    private static class LogInvocationHandler<T> implements InvocationHandler {
        private final T object;
        private final List<String> list = new ArrayList<>();

        LogInvocationHandler(T object) {
            this.object = object;
            addLogAnnotation();
        }

        private void addLogAnnotation() {
            Method[] methods = object.getClass().getMethods();
            list.addAll(Arrays.stream(methods)
                    .filter(m -> m.isAnnotationPresent(Log.class))
                    .map(m -> m.getName() + Arrays.toString(m.getParameterTypes()))
                    .toList());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (list.contains(method.getName() + Arrays.toString(method.getParameterTypes()))) {
                System.out.printf("Executed method: %s, params: %s%n", method.getName(), Arrays.toString(args));
            }
            return method.invoke(object, args);
        }
    }
}
