package ru.otus.jdbc.mapper;

public class MetaDataException extends RuntimeException {
    public MetaDataException(String message) {
        super(message);
    }

    public MetaDataException() {
        super();
    }
}
