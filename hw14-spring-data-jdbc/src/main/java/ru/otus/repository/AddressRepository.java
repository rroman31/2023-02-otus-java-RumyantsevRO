package ru.otus.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.model.Client;

public interface AddressRepository extends ListCrudRepository<Client, Long> {
}
