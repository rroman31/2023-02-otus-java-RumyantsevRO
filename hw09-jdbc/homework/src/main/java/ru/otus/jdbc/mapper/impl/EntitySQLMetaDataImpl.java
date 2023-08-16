package ru.otus.jdbc.mapper.impl;

import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {
    private final String GET_ALL_FIELDS_TEMPLATE = "select %s from %s";
    private final String SELECT_BY_ID_TEMPLATE = "select %s from %s where %s=?";
    private final String INSERT_TEMPLATE = "insert into %s(%s) values (%s)";
    private final String UPDATE_TEMPLATE = "update %s set %s where %s = ?";

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public String getSelectAllSql() {
        return String.format(GET_ALL_FIELDS_TEMPLATE, getFieldsNamesAsString(), entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        return String.format(SELECT_BY_ID_TEMPLATE, getFieldsNamesAsString(), entityClassMetaData.getName(), entityClassMetaData.getIdField().getName());

    }

    @Override
    public String getInsertSql() {
        var fieldsWithoutId = getFieldsNamesWithoutId();
        var fieldsString = String.join(", ", fieldsWithoutId);
        return String.format(INSERT_TEMPLATE, entityClassMetaData.getName(), fieldsString, parametersString(fieldsWithoutId.size()));
    }

    @Override
    public String getUpdateSql() {
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        return String.format(UPDATE_TEMPLATE, entityClassMetaData.getName(), createParametersListForUpdate(fields), entityClassMetaData.getIdField().getName());
    }

    private String getFieldsNamesAsString() {
        return String.join(",", getFieldsNames());
    }

    private List<String> getFieldsNamesWithoutId() {
        List<Field> listOfFields = entityClassMetaData.getFieldsWithoutId();
        return listOfFields.stream().map(Field::getName).toList();
    }

    private List<String> getFieldsNames() {
        List<Field> listOfFields = entityClassMetaData.getAllFields();
        return listOfFields.stream().map(Field::getName).toList();
    }

    private String parametersString(int parametersCount) {
        var parametersString = new StringJoiner(",");
        for (int i = 0; i < parametersCount; i++) {
            parametersString.add("?");
        }
        return parametersString.toString();
    }

    private String createParametersListForUpdate(List<Field> parameters) {
        var parametersString = new StringJoiner(",");
        for (Field parameter : parameters) {
            parametersString.add(String.format("%s=?", parameter.getName()));
        }
        return parametersString.toString();
    }


}
