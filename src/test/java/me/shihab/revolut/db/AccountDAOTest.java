package me.shihab.revolut.db;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import me.shihab.revolut.core.AccountEntity;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DropwizardExtensionsSupport.class)
class AccountDAOTest {
    private final DAOTestExtension daoTestExtension = DAOTestExtension.newBuilder().addEntityClass(AccountEntity.class).build();

    private AccountDAO accountDAO = new AccountDAO(daoTestExtension.getSessionFactory());

    private AccountEntity getTestAccountEntity() {
        String testAccountName = "Test 1";
        BigDecimal testBalance = BigDecimal.valueOf(100.00);
        return new AccountEntity(testAccountName, testBalance);
    }

    @Test
    void extensionCreatedSessionFactory_isNotNull() {
        final SessionFactory sessionFactory = daoTestExtension.getSessionFactory();

        assertThat(sessionFactory).isNotNull();
    }

    @Test
    void upsert_ShouldReturnNewlyCreatedAccountEntity() {
        AccountEntity testAccountToBeCreated = getTestAccountEntity();

        AccountEntity createdAccount = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(testAccountToBeCreated));

        assertThat(createdAccount).isEqualToComparingFieldByField(testAccountToBeCreated);
    }

    @Test
    void upsert_ShouldUpdateAccountEntityCorrectly() {
        AccountEntity accountToBeCreated = getTestAccountEntity();

        AccountEntity createdAccountEntity = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(accountToBeCreated));

        String testNewAccountName = "Some Other Name";
        BigDecimal testNewAmount = BigDecimal.valueOf(300.00);

        createdAccountEntity.setName(testNewAccountName);
        createdAccountEntity.setBalance(testNewAmount);

        AccountEntity updatedAccountEntity = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(createdAccountEntity));

        assertThat(updatedAccountEntity).isEqualToComparingFieldByField(createdAccountEntity);
    }

    @Test
    void upsert_IfExceptionOccurred_RollBacksSuccessfully() {
        String testAccountName = "Test 1";
        BigDecimal testBalance = BigDecimal.valueOf(100.00);
        AccountEntity accountToBeCreated = new AccountEntity(testAccountName, testBalance);

        daoTestExtension.inTransaction(() -> accountDAO.upsert(accountToBeCreated));

        accountToBeCreated.setBalance(BigDecimal.valueOf(300.00));
        try {
            daoTestExtension.inTransaction(() -> {
                accountDAO.upsert(accountToBeCreated);
                accountDAO.upsert(new AccountEntity(null, null));
            });
            failBecauseExceptionWasNotThrown(PersistenceException.class);
        } catch (PersistenceException ignoredException) {
            final AccountEntity sameAccountEntity = accountDAO.findById(accountToBeCreated.getId());
            assertThat(sameAccountEntity.getName()).isEqualTo(testAccountName);
            assertThat(sameAccountEntity.getBalance()).isEqualByComparingTo(testBalance);
        }
    }

    @Test
    void findById_GivenValidAccountId_ReturnsCorrectAccount() {
        final AccountEntity testAccountToBeCreated = getTestAccountEntity();

        daoTestExtension.inTransaction(() -> accountDAO.upsert(testAccountToBeCreated));

        AccountEntity createdAccount = daoTestExtension.inTransaction(() -> accountDAO.findById(testAccountToBeCreated.getId()));

        assertThat(createdAccount).isEqualToComparingFieldByField(testAccountToBeCreated);
    }

    @Test
    void findById_GivenInvalidAccountId_ReturnsNull() {
        String testAccountName = "Test 3";
        BigDecimal testBalance = BigDecimal.valueOf(200.00);
        final AccountEntity testAccountToBeCreated = new AccountEntity(testAccountName, testBalance);

        daoTestExtension.inTransaction(() -> accountDAO.upsert(testAccountToBeCreated));

        AccountEntity createdAccount = daoTestExtension.inTransaction(() -> accountDAO.findById(10));

        assertNull(createdAccount);
    }

}