package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.cache.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.List;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
///
        MyCache<String, Client> cache = new MyCache<>(100);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        var firstClient = dbServiceClient.saveClient(createClient("dbServiceFirst", "first_street", List.of("first_client_first_number", "first_client_second_number")));
        var secondClient = dbServiceClient.saveClient(createClient("dbServiceSecond", "second_street", List.of("second_client_first_number", "second_client_second_number")));

        var clientSecondSelected = dbServiceClient.getClient(secondClient.getId()).orElseThrow(() -> new RuntimeException("Client not found, id:" + secondClient.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);
        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated"));
        var clientUpdated = dbServiceClient.getClient(clientSecondSelected.getId()).orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }

    private static Client createClient(String clientName, String street, List<String> phoneNumbers) {
        var client = new Client(clientName);

        phoneNumbers.forEach(s -> {
            var phone = new Phone(s);
            client.addPhone(phone);
        });

        client.addAddress(new Address(street));
        return client;
    }
}