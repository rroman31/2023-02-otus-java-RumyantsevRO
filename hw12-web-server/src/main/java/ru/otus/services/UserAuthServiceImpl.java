package ru.otus.services;

import ru.otus.data.crm.service.DbServiceAppUser;

public class UserAuthServiceImpl implements UserAuthService {

    private final DbServiceAppUser dbServiceAppUser;

    public UserAuthServiceImpl(DbServiceAppUser dbServiceAppUser) {
        this.dbServiceAppUser = dbServiceAppUser;
    }

    @Override
    public boolean authenticate(String login, String password) {
        return dbServiceAppUser.findByLogin(login).map(user -> user.getPassword().equals(password)).orElse(false);
    }

}
