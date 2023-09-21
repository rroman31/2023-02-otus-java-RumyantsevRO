package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.data.core.repository.DataTemplateHibernate;
import ru.otus.data.core.repository.HibernateUtils;
import ru.otus.data.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.data.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.data.crm.model.Address;
import ru.otus.data.crm.model.AppUser;
import ru.otus.data.crm.model.Client;
import ru.otus.data.crm.model.Phone;
import ru.otus.data.crm.service.DbServiceAppUserImpl;
import ru.otus.data.crm.service.DbServiceClientImpl;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerImpl;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerWithFilterBasedSecurityDemo {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {

        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        executeMigration(configuration);
        var transactionManager = createTransactionManager(configuration);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        var appUserTemplate = new DataTemplateHibernate<>(AppUser.class);
        var dbServiceAppUser = new DbServiceAppUserImpl(transactionManager, appUserTemplate);

        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(dbServiceAppUser);

        UsersWebServer usersWebServer = new UsersWebServerImpl(WEB_SERVER_PORT, gson, templateProcessor, authService, dbServiceClient);

        usersWebServer.start();
        usersWebServer.join();
    }

    private static TransactionManagerHibernate createTransactionManager(Configuration configuration) {
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class, AppUser.class);

        return new TransactionManagerHibernate(sessionFactory);
    }

    public static void executeMigration(Configuration configuration) {
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");
        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();
    }

}
