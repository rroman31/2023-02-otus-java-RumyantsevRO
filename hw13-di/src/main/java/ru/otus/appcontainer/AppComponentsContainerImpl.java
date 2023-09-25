package ru.otus.appcontainer;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exception.AppComponentsCreationException;

import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unchecked")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Map<String, Object> appComponentsByName = new HashMap<>();

    private final Map<String, List<Object>> appAppComponentsByFqdn = new HashMap<>();

    public AppComponentsContainerImpl(String packageForScan) {
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(packageForScan)));
        Map<Integer, Class<?>> configurations = new TreeMap<>();
        reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class).stream().forEach(e -> {
            AppComponentsContainerConfig annotation = e.getAnnotation(AppComponentsContainerConfig.class);
            configurations.put(annotation.order(), e);
        });

        List<Class<?>> sortedConfiguration = configurations.values().stream().toList();
        processConfig(sortedConfiguration);
    }

    public AppComponentsContainerImpl(Class<?>... configClasses) {
        processConfig(Arrays.stream(configClasses).toList());
    }

    private AppComponentsContainerImpl(Class<?> configClass) {
        processConfig(Arrays.asList(configClass));
    }

    private void processConfig(List<Class<?>> configClasses) {
        checkConfigClass(configClasses);
        List<ClassDefinition> classDefinitions = new ArrayList<>();
        for (Class<?> configClass : configClasses) {
            Object instanceOfConfiguration;
            try {
                instanceOfConfiguration = configClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new AppComponentsCreationException("Config class create instance error", e);
            }

            Map<Integer, List<ClassDefinition>> configProcessingMetadata = new TreeMap<>();
            List<Method> methods = Arrays.stream(configClass.getDeclaredMethods()).toList();
            for (Method method : methods) {
                var annotation = method.getAnnotation(AppComponent.class);
                if (annotation != null) {
                    configProcessingMetadata.computeIfAbsent(annotation.order(), k -> new ArrayList<>()).add(new ClassDefinition(annotation.order(), method.getReturnType(), annotation.name(), method, configClass, instanceOfConfiguration, Arrays.stream(method.getParameterTypes()).map(Class::getName).toList()));
                }
            }
            classDefinitions.addAll(configProcessingMetadata.values().stream().flatMap(List::stream).toList());
        }
        createInstancesByBeenDefinitions(classDefinitions);
    }

    private void checkConfigClass(List<Class<?>> configClasses) {
        configClasses.forEach(this::checkConfigClass);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<C> result = (List<C>) appAppComponentsByFqdn.get(componentClass.getName());
        if (Objects.nonNull(result) && result.size() > 1) {
            throw new AppComponentsCreationException(String.format("Found more then one instance of Class: %s", componentClass.getName()));
        }
        if (Objects.nonNull(result) && result.size() == 1) {
            return result.get(0);
        }
        try {
            var interfaces = componentClass.getInterfaces();
            Optional<Class<?>> interfaceObj = Arrays.stream(interfaces).filter(interfaceInstance -> Objects.nonNull(appAppComponentsByFqdn.get(interfaceInstance.getName()))).findFirst();
            List<C> objects = interfaceObj.map(aClass -> (List<C>) appAppComponentsByFqdn.get(aClass.getName())).orElseThrow(RuntimeException::new);
            if (Objects.nonNull(objects) && objects.size() > 1) {
                throw new AppComponentsCreationException(String.format("Found more then one instance of Class: %s", componentClass.getName()));
            }
            if (Objects.nonNull(objects) && objects.size() == 1) {
                return objects.get(0);
            } else {
                throw new AppComponentsCreationException(String.format("Not found instance of Class: %s", componentClass.getName()));
            }
        } catch (Exception e) {
            throw new AppComponentsCreationException("Class not found");
        }
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        var result = (C) appComponentsByName.get(componentName);
        if (Objects.isNull(result)) {
            throw new AppComponentsCreationException();
        }
        return result;
    }

    private void createInstancesByBeenDefinitions(List<ClassDefinition> classDefinitions) {
        classDefinitions.forEach(e -> {
            try {
                Object result;
                if (e.getDependenciesTypeNames().isEmpty()) {
                    result = e.getMethodForInstanceCreation().invoke(e.getInstanceOfConfiguration());
                } else {
                    result = e.getMethodForInstanceCreation().invoke(e.getInstanceOfConfiguration(), getArguments(e.getDependenciesTypeNames()).toArray());
                }
                if (appComponentsByName.get(e.getName()) != null) {
                    throw new AppComponentsCreationException("Found duplicate names");
                }
                appComponentsByName.put(e.getName(), result);
                appAppComponentsByFqdn.computeIfAbsent(e.getMethodForInstanceCreation().getReturnType().getName(), k -> new ArrayList<>()).add(result);
            } catch (Exception exception) {
                throw new AppComponentsCreationException("CreateInstancesByBeenDefinitions", exception);
            }
        });
    }

    private List<Object> getArguments(List<String> listOfArgumentsNames) {
        return listOfArgumentsNames.stream().map(e -> {
            List<Object> args = appAppComponentsByFqdn.get(e);
            if (args.size() > 1) {
                throw new AppComponentsCreationException(String.format("Found more then one instance of Class: %s", e));
            }
            return args.isEmpty() ? null : args.get(0);
        }).toList();
    }

    private class ClassDefinition {
        private final int order;
        private final Class<?> type;
        private final String name;
        private final Method methodForInstanceCreation;
        private final Class<?> typeOfConfigurations;
        private final Object instanceOfConfiguration;

        private List<String> dependenciesTypeNames;

        public ClassDefinition(int order, Class<?> type, String name, Method methodForInstanceCreation, Class<?> typeOfConfigurations, Object instanceOfConfiguration, List<String> dependenciesTypeNames) {
            this.order = order;
            this.type = type;
            this.name = name;
            this.methodForInstanceCreation = methodForInstanceCreation;
            this.typeOfConfigurations = typeOfConfigurations;
            this.instanceOfConfiguration = instanceOfConfiguration;
            this.dependenciesTypeNames = dependenciesTypeNames;
        }

        public int getOrder() {
            return order;
        }

        public Class<?> getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public Method getMethodForInstanceCreation() {
            return methodForInstanceCreation;
        }

        public Class<?> getTypeOfConfigurations() {
            return typeOfConfigurations;
        }

        public Object getInstanceOfConfiguration() {
            return instanceOfConfiguration;
        }

        public List<String> getDependenciesTypeNames() {
            return dependenciesTypeNames;
        }
    }
}


