package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.cache.HwListener;
import ru.otus.core.cache.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;

import static ru.otus.demo.DbServiceDemo.HIBERNATE_CFG_FILE;

public class DBServiceCacheDemo {
    private static final Logger logger = LoggerFactory.getLogger(DBServiceCacheDemo.class);

    public static void main(String[] args) {

        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);

        HwListener<String, Client> listener = new HwListener<String, Client>() {
            @Override
            public void notify(String key, Client value, String action) {
                logger.info("key:{}, value:{}, action: {}", key, value, action);
            }
        };

        MyCache<String, Client> cache = new MyCache<>(100);
        cache.addListener(listener);

        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, cache);

        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            clients.add(dbServiceClient.saveClient(createClient("cliet_" + i, i + "_street", List.of(i + "_client_first_number", i + "_client_second_number"))));
        }

        var cacheState = cache.getCacheState();
        logger.info("Cache size: {}; listeners count: {}", cacheState.cacheSize(), cacheState.listenerCount());

        clients.forEach(client -> dbServiceClient.getClient(client.getId()));

        dbServiceClient.remove(clients.get(0));

        listener = null;
        System.gc();

        dbServiceClient.remove(clients.get(1));

        cacheState = cache.getCacheState();
        logger.info("Cache size: {}; listeners count: {}", cacheState.cacheSize(), cacheState.listenerCount());

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
