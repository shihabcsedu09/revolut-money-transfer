package me.shihab.revolut.service.impl;

import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.core.AccountEntity;
import me.shihab.revolut.db.AccountDAO;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.mapper.AccountMapper;
import me.shihab.revolut.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceImplTest {

    private final AccountDAO accountDao = mock(AccountDAO.class);

    private final AccountMapper accountMapper = mock(AccountMapper.class);

    private AccountService accountService;


    @BeforeEach
    void setup() {
        accountService = new AccountServiceImpl(accountDao, accountMapper);
    }

    @Test
    void create_GivenValidAccountDTO_ShouldReturnMappedDTO() {
        long testAccountId = 123;
        String testAccountName = "Some Account Name";
        BigDecimal testAmount = BigDecimal.valueOf(100.00);

        AccountDTO testAccountDTO = new AccountDTO(testAccountId, testAccountName, testAmount);

        AccountEntity testAccountEntityToBeCreated = new AccountEntity(testAccountName, testAmount);
        AccountEntity testAccountEntityCreated = new AccountEntity(testAccountName, testAmount);
        testAccountEntityCreated.setId(testAccountId);

        when(accountMapper.dtoToEntity(testAccountDTO)).thenReturn(testAccountEntityToBeCreated);
        when(accountDao.upsert(testAccountEntityToBeCreated)).thenReturn(testAccountEntityCreated);
        when(accountMapper.entityToDto(testAccountEntityCreated)).thenReturn(testAccountDTO);

        assertEquals(testAccountDTO, accountService.create(testAccountDTO));
    }

    @Test
    void get_GivenNonExistingAccountId_ShouldThrowRuntimeException() {
        long testAccountId = 123;
        when(accountDao.findById(testAccountId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> accountService.get(testAccountId));
    }

    @Test
    void get_GivenExistingAccountId_ShouldReturnCorrectAccountDTO() throws RuntimeException {
        long testAccountId = 123;
        String testAccountName = "Some Account Name";
        BigDecimal testAmount = BigDecimal.valueOf(100.00);

        AccountDTO testAccountDTO = new AccountDTO(testAccountId, testAccountName, testAmount);
        AccountEntity testAccountEntity = new AccountEntity(testAccountName, testAmount);
        testAccountEntity.setId(testAccountId);

        when(accountDao.findById(testAccountId)).thenReturn(testAccountEntity);
        when(accountMapper.entityToDto(testAccountEntity)).thenReturn(testAccountDTO);

        assertEquals(testAccountDTO, accountService.get(testAccountId));
    }
}