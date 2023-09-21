package ru.otus.data.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.data.core.repository.DataTemplate;
import ru.otus.data.core.sessionmanager.TransactionManager;
import ru.otus.data.crm.model.AppUser;

import java.util.List;
import java.util.Optional;

public class DbServiceAppUserImpl implements DbServiceAppUser {

    private static final Logger log = LoggerFactory.getLogger(DbServiceAppUserImpl.class);

    private final DataTemplate<AppUser> appUserDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceAppUserImpl(TransactionManager transactionManager, DataTemplate<AppUser> appUserDataTemplate) {
        this.transactionManager = transactionManager;
        this.appUserDataTemplate = appUserDataTemplate;
    }

    @Override
    public Optional<AppUser> findById(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var appUserOptional = appUserDataTemplate.findById(session, id);
            log.info("client: {}", appUserOptional);
            return appUserOptional;
        });
    }

    @Override
    public Optional<AppUser> findByLogin(String login) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var query = session.createQuery("select appUser from AppUser appUser where appUser.login=:login", AppUser.class);
            query.setParameter("login", login);
            return query.uniqueResultOptional();
        });
    }

    @Override
    public List<AppUser> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = appUserDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
