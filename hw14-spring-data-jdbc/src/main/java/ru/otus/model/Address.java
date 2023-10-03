package ru.otus.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Table("address")
@Getter
public class Address {
    @Id
    @NonNull
    private final Long id;
    private final String street;

    public Address(String street, Long clientId) {
        this(null, street);
    }

    @Default
    @PersistenceCreator
    public Address(@NonNull Long id, String street) {
        this.id = id;
        this.street = street;
    }
}
