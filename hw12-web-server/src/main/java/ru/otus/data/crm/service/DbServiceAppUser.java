package ru.otus.data.crm.service;

import ru.otus.data.crm.model.AppUser;

import java.util.List;
import java.util.Optional;

public interface DbServiceAppUser {
    Optional<AppUser> findById(long id);

    Optional<AppUser> findByLogin(String login);

    List<AppUser> findAll();
}
