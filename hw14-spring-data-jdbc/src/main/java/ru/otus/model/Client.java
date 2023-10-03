package ru.otus.model;


import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.Set;

@Table("client")
@Getter
public class Client {

    @Id
    @NonNull
    private final Long id;

    private final String name;

    @MappedCollection(idColumn = "client_id")
    private final Address address;

    @MappedCollection(idColumn = "client_id", keyColumn = "client_id")
    private final Set<Phone> phone;

    @Default
    @PersistenceCreator
    public Client(@NonNull Long id, String name, Address address, Set<Phone> phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;

    }

    public Client(String name, Address address, Set<Phone> phone) {
        this(null, name, address, phone);
    }


}
