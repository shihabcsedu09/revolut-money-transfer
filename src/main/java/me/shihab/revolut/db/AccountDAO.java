package me.shihab.revolut.db;

import io.dropwizard.hibernate.AbstractDAO;
import me.shihab.revolut.core.AccountEntity;
import org.hibernate.SessionFactory;

public class AccountDAO extends AbstractDAO<AccountEntity> {
    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public AccountEntity upsert(AccountEntity accountEntity) {
        return persist(accountEntity);
    }

    public AccountEntity findById(long id) {
        return currentSession().get(AccountEntity.class, id);
    }

}
