package me.shihab.revolut.db;

import io.dropwizard.hibernate.AbstractDAO;
import me.shihab.revolut.core.AccountEntity;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDAO extends AbstractDAO<AccountEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDAO.class);

    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public AccountEntity upsert(AccountEntity accountEntity) {
        LOGGER.debug("Persisting Account : " + accountEntity.toString());
        return persist(accountEntity);
    }

    public AccountEntity findById(long id) {
        LOGGER.debug("Fetching account info for id : " + id);
        return currentSession().get(AccountEntity.class, id);
    }

}
