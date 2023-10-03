package ru.otus.service;

import ru.otus.model.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();

    Client findById(long id);

    Client save(Client client);

    Client update(Client client, long id);

    void removeById(long id);
}
