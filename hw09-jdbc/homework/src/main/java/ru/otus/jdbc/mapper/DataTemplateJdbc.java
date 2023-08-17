package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.crm.model.Id;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData<T> entitySQLMetaData;
    private final Class<T> clazz;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData<T> entitySQLMetaData, Class<T> clazz) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.clazz = clazz;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    List<FieldDescriptor> fieldsDescriptorsList = new ArrayList<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        fieldsDescriptorsList.add(new FieldDescriptor(rs.getMetaData().getColumnClassName(i), rs.getObject(i), rs.getMetaData().getColumnLabel(i)));
                    }
                    return constructInstance(fieldsDescriptorsList);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException("findById call error", e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
            List<T> entityList = new ArrayList<>();
            try {
                while (rs.next()) {
                    List<FieldDescriptor> fieldsDescriptorsList = new ArrayList<>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        fieldsDescriptorsList.add(new FieldDescriptor(rs.getMetaData().getColumnClassName(i), rs.getObject(i), rs.getMetaData().getColumnLabel(i)));
                    }
                    entityList.add(constructInstance(fieldsDescriptorsList));
                }
                return entityList;
            } catch (SQLException ex) {
                throw new DataTemplateException("findAll call error", ex);
            }
        }).orElseThrow(() -> new DataTemplateException("findAll call error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getFieldsValues(entity));
        } catch (Exception e) {
            throw new DataTemplateException("Insert error", e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), getFieldsValuesForUpdate(entity));
        } catch (Exception e) {
            throw new DataTemplateException("Update error", e);
        }
    }

    private T constructInstance(List<FieldDescriptor> fieldDescriptors) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (FieldDescriptor fieldDescriptor : fieldDescriptors) {
                Method fieldSetter = clazz.getDeclaredMethod(String.format("set%s", fieldDescriptor.name.substring(0, 1).toUpperCase() + fieldDescriptor.name.substring(1)), Class.forName(fieldDescriptor.classFdn));
                fieldSetter.invoke(instance, fieldDescriptor.value);
            }
            return instance;
        } catch (Exception e) {
            throw new DataTemplateException("constructInstance error", e);
        }
    }

    private List<Object> getFieldsValues(T entity) {
        List<Field> allFields = new ArrayList<>(Arrays.stream(clazz.getDeclaredFields()).toList());
        List<Field> markedAsIdField = allFields.stream().filter(this::isFieldMarkedAsId).toList();

        if (markedAsIdField.size() != 1) {
            throw new DataTemplateException("Нет поля помеченного @Id");
        }
        allFields.remove(markedAsIdField.get(0));

        return allFields.stream().map(e -> {
            e.setAccessible(true);
            try {
                return e.get(entity);
            } catch (IllegalAccessException ex) {
                throw new DataTemplateException("Unexpected error", ex);
            }
        }).toList();
    }

    private List<Object> getFieldsValuesForUpdate(T entity) {
        List<Field> allFields = new ArrayList<>(Arrays.stream(clazz.getDeclaredFields()).toList());
        List<Field> markedAsIdField = allFields.stream().filter(this::isFieldMarkedAsId).toList();

        if (markedAsIdField.size() != 1) {
            throw new MetaDataException("Нет поля помеченного @Id");
        }
        allFields.remove(markedAsIdField.get(0));
        allFields.add(markedAsIdField.get(0));

        return allFields.stream().map(e -> {
            e.setAccessible(true);
            try {
                return e.get(entity);
            } catch (IllegalAccessException ex) {
                throw new DataTemplateException("Unexpected error", ex);
            }
        }).toList();
    }

    private boolean isFieldMarkedAsId(Field field) {
        return Arrays.stream(field.getDeclaredAnnotations()).anyMatch(Id.class::isInstance);
    }

    private static class FieldDescriptor {
        String classFdn;
        Object value;
        String name;

        FieldDescriptor(String classFdn, Object value, String name) {
            this.classFdn = classFdn;
            this.value = value;
            this.name = name;
        }
    }
}
