package ru.otus.jdbc.mapper.impl;

import ru.otus.crm.model.Id;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.MetaDataException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            return clazz.getConstructor();
        } catch (Exception e) {
            throw new MetaDataException(String.format("Не удалось получить конструктор каласса: %s", getName()));
        }
    }

    @Override
    public Field getIdField() {
        var filteredFields = getAllFields().stream().filter(this::isFieldMarkedAsId).toList();

        if (filteredFields.size() == 1) {
            return filteredFields.get(0);
        }
        if (filteredFields.size() > 1) {
            throw new MetaDataException("Более одного поля помечено аннотацией @Id");
        }

        throw new MetaDataException("Нет поля помеченного @Id");

    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.stream(clazz.getDeclaredFields()).toList();
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        var allFields = new ArrayList<>(getAllFields());
        allFields.remove(getIdField());
        return Collections.unmodifiableList(allFields);
    }

    private boolean isFieldMarkedAsId(Field field) {
        return Arrays.stream(field.getDeclaredAnnotations()).anyMatch(Id.class::isInstance);
    }

}
