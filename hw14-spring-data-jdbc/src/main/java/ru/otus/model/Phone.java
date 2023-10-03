package ru.otus.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

@Table("phone")
@Getter
public class Phone {
    @Id
    private final Long id;

    @Nonnull
    private final Long clientId;

    @Nonnull
    private final String number;


    public Phone(@Nonnull Long clientId, @Nonnull String number) {
        this(null, clientId, number);
    }

    @Default
    @PersistenceCreator
    public Phone(Long id, @Nonnull Long clientId, @Nonnull String number) {
        this.id = id;
        this.clientId = clientId;
        this.number = number;
    }
}
