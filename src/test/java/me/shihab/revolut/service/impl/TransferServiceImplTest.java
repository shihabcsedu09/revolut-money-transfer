package me.shihab.revolut.service.impl;

import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.core.AccountEntity;
import me.shihab.revolut.core.TransactionEntity;
import me.shihab.revolut.db.AccountDAO;
import me.shihab.revolut.db.TransactionDAO;
import me.shihab.revolut.exception.FailureMessage;
import me.shihab.revolut.exception.FailureStatusCode;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.mapper.TransactionMapper;
import me.shihab.revolut.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;


class TransferServiceImplTest {
    private final AccountDAO accountDAO = mock(AccountDAO.class);

    private final TransactionMapper transactionMapper = mock(TransactionMapper.class);

    private final TransactionDAO transactionDAO = mock(TransactionDAO.class);

    private TransferService transferService;

    @BeforeEach
    void setup() {
        transferService = new TransferServiceImpl(accountDAO, transactionDAO, transactionMapper);
    }

    @Test
    void transfer_GivenNonExistingSourceAccountId_ShouldThrowAppropriateException() {
        TransferDTO transferDTO = new TransferDTO(1L, 2L, BigDecimal.valueOf(100));

        when(accountDAO.findById(transferDTO.getFromAccountId())).thenReturn(null);

        RuntimeException accountNotFoundException = assertThrows(RuntimeException.class,
                () -> transferService.transfer(transferDTO));

        assertEquals(FailureStatusCode.SOURCE_ACCOUNT_NOT_FOUND.statusCode(), accountNotFoundException.getFailureResponse().getStatusCode());
        assertEquals(FailureMessage.SOURCE_ACCOUNT_NOT_FOUND.message(), accountNotFoundException.getFailureResponse().getMessage());
    }

    @Test
    void transfer_GivenNonExistingDestinationAccountIt_ShouldThrowAppropriateException() {
        TransferDTO transferDTO = new TransferDTO(1L, 2L, BigDecimal.valueOf(100));

        when(accountDAO.findById(transferDTO.getFromAccountId())).thenReturn(new AccountEntity());
        when(accountDAO.findById(transferDTO.getToAccountId())).thenReturn(null);

        RuntimeException accountNotFoundException = assertThrows(RuntimeException.class,
                () -> transferService.transfer(transferDTO));

        assertEquals(FailureStatusCode.DESTINATION_ACCOUNT_NOT_FOUND.statusCode(), accountNotFoundException.getFailureResponse().getStatusCode());
        assertEquals(FailureMessage.DESTINATION_ACCOUNT_NOT_FOUND.message(), accountNotFoundException.getFailureResponse().getMessage());
    }


    @Test
    void transfer_GivenSourceAccountWithInsufficientBalance_ShouldThrowAppropriateException() {
        TransferDTO transferDTO = new TransferDTO(1L, 2L, BigDecimal.valueOf(100));

        AccountEntity fromAccount = new AccountEntity("from account name", BigDecimal.valueOf(50));
        AccountEntity toAccount = new AccountEntity("to account name", BigDecimal.valueOf(100));

        when(accountDAO.findById(transferDTO.getFromAccountId())).thenReturn(fromAccount);
        when(accountDAO.findById(transferDTO.getToAccountId())).thenReturn(toAccount);

        RuntimeException accountNotFoundException = assertThrows(RuntimeException.class,
                () -> transferService.transfer(transferDTO));

        assertEquals(FailureStatusCode.NOT_ENOUGH_BALANCE.statusCode(), accountNotFoundException.getFailureResponse().getStatusCode());
        assertEquals(FailureMessage.NOT_ENOUGH_BALANCE.message(), accountNotFoundException.getFailureResponse().getMessage());
    }

    @Test
    void transfer_GiveValidTransferRequest_ShouldCallInternalMethodsWithProperParameters() throws RuntimeException {
        TransferDTO transferDTO = new TransferDTO(1L, 2L, BigDecimal.valueOf(20));
        AccountEntity fromAccount = new AccountEntity("from account name", BigDecimal.valueOf(50));
        AccountEntity toAccount = new AccountEntity("to account name", BigDecimal.valueOf(100));

        AccountEntity updatedFromAccount = new AccountEntity("from account name", BigDecimal.valueOf(30));
        AccountEntity updatedToAccount = new AccountEntity("to account name", BigDecimal.valueOf(120));
        TransactionEntity testTransactionEntity = new TransactionEntity(fromAccount.getId(),
                toAccount.getId(),
                transferDTO.getAmount());

        when(accountDAO.findById(transferDTO.getFromAccountId())).thenReturn(fromAccount);
        when(accountDAO.findById(transferDTO.getToAccountId())).thenReturn(toAccount);
        when(transactionDAO.upsert(any(TransactionEntity.class))).thenReturn(testTransactionEntity);

        transferService.transfer(transferDTO);

        ArgumentCaptor<AccountEntity> accountEntityArgCaptor = ArgumentCaptor.forClass(AccountEntity.class);
        ArgumentCaptor<TransactionEntity> transactionEntityArgCaptor = ArgumentCaptor.forClass(TransactionEntity.class);

        verify(accountDAO, times(2)).upsert(accountEntityArgCaptor.capture());
        verify(transactionDAO, times(1)).upsert(transactionEntityArgCaptor.capture());

        assertThat(updatedFromAccount).isEqualToComparingFieldByField(accountEntityArgCaptor.getAllValues().get(0));
        assertThat(updatedToAccount).isEqualToComparingFieldByField(accountEntityArgCaptor.getAllValues().get(1));

        assertThat(testTransactionEntity).isEqualToComparingFieldByField(transactionEntityArgCaptor.getAllValues().get(0));
    }
}