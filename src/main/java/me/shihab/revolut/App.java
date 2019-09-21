package me.shihab.revolut;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import me.shihab.revolut.core.AccountEntity;
import me.shihab.revolut.core.Transaction;
import me.shihab.revolut.db.AccountDAO;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.exception.UncaughtException;
import me.shihab.revolut.mapper.AccountMapper;
import me.shihab.revolut.resources.AccountResource;
import me.shihab.revolut.service.AccountService;
import me.shihab.revolut.service.impl.AccountServiceImpl;

public class App extends Application<AppConfiguration> {

    private HibernateBundle<AppConfiguration> hibernateBundle = new HibernateBundle<AppConfiguration>(AccountEntity.class,
            Transaction.class) {
        public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public String getName() {
        return "App";
    }

    @Override
    public void initialize(final Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(final AppConfiguration configuration,
                    final Environment environment) {
        final AccountDAO accountDAO = new AccountDAO(hibernateBundle.getSessionFactory());
        final AccountMapper accountMapper = new AccountMapper();
        final AccountService accountService = new AccountServiceImpl(accountDAO, accountMapper);

        environment.jersey().register(new AccountResource(accountService));
        environment.jersey().register(new RuntimeException());
        environment.jersey().register(new UncaughtException());
    }

}
