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

    @Test
    void extensionCreatedSessionFactory() {
        final SessionFactory sessionFactory = daoTestExtension.getSessionFactory();

        assertThat(sessionFactory).isNotNull();
    }

    @Test
    void upsert_ShouldReturnNewlyCreatedAccountEntity() {
        String testAccountName = "Test 1";
        BigDecimal testAmount = BigDecimal.valueOf(100.00);
        AccountEntity testAccountToBeCreated = new AccountEntity(testAccountName, testAmount);

        AccountEntity createdAccount = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(testAccountToBeCreated));

        assertThat(createdAccount).isEqualToComparingFieldByField(testAccountToBeCreated);
    }

    @Test
    void upsert_ShouldUpdateAccountEntityCorrectly() {
        String testAccountNameToBeCreated = "Test 2";
        BigDecimal testAmount = BigDecimal.valueOf(200.00);
        AccountEntity accountToBeCreated = new AccountEntity(testAccountNameToBeCreated, testAmount);

        AccountEntity createdAccountEntity = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(accountToBeCreated));

        String testNewAccountName = "Some Other Name";
        BigDecimal testNewAmount = BigDecimal.valueOf(300.00);

        createdAccountEntity.setName(testNewAccountName);
        createdAccountEntity.setAmount(testNewAmount);

        AccountEntity updatedAccountEntity = daoTestExtension.inTransaction(() ->
                accountDAO.upsert(accountToBeCreated));

        assertThat(updatedAccountEntity).isEqualToComparingFieldByField(accountToBeCreated);
    }

    @Test
    void upsert_IfExceptionOccurred_RollBacksSuccessfully() {
        String testAccountNameToBeCreated = "Test 3";
        BigDecimal testAmount = BigDecimal.valueOf(200.00);
        final AccountEntity testAccountToBeCreated = new AccountEntity(testAccountNameToBeCreated, testAmount);

        daoTestExtension.inTransaction(() -> accountDAO.upsert(testAccountToBeCreated));

        testAccountToBeCreated.setAmount(BigDecimal.valueOf(300.00));
        try {
            daoTestExtension.inTransaction(() -> {
                accountDAO.upsert(testAccountToBeCreated);
                accountDAO.upsert(new AccountEntity(null, null));
            });
            failBecauseExceptionWasNotThrown(PersistenceException.class);
        } catch (PersistenceException ignoredException) {
            final AccountEntity sameAccountEntity = accountDAO.findById(testAccountToBeCreated.getId());
            assertThat(sameAccountEntity.getName()).isEqualTo(testAccountNameToBeCreated);
            assertThat(sameAccountEntity.getAmount()).isEqualByComparingTo(testAmount);
        }
    }

    @Test
    void findById_GivenValidAccountId_ReturnsCorrectAccount() {
        String testAccountName = "Test 3";
        BigDecimal testAmount = BigDecimal.valueOf(200.00);
        final AccountEntity testAccountToBeCreated = new AccountEntity(testAccountName, testAmount);

        daoTestExtension.inTransaction(() -> accountDAO.upsert(testAccountToBeCreated));

        AccountEntity createdAccount = daoTestExtension.inTransaction(() -> accountDAO.findById(testAccountToBeCreated.getId()));

        assertThat(createdAccount).isEqualToComparingFieldByField(testAccountToBeCreated);
    }

    @Test
    void findById_GivenInvalidAccountId_ReturnsNull() {
        String testAccountName = "Test 3";
        BigDecimal testAmount = BigDecimal.valueOf(200.00);
        final AccountEntity testAccountToBeCreated = new AccountEntity(testAccountName, testAmount);

        daoTestExtension.inTransaction(() -> accountDAO.upsert(testAccountToBeCreated));

        AccountEntity createdAccount = daoTestExtension.inTransaction(() -> accountDAO.findById(10));

        assertNull(createdAccount);
    }

}