package me.shihab.revolut.service.impl;

import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.core.AccountEntity;
import me.shihab.revolut.core.TransactionEntity;
import me.shihab.revolut.db.AccountDAO;
import me.shihab.revolut.db.TransactionDAO;
import me.shihab.revolut.exception.FailureMessage;
import me.shihab.revolut.exception.FailureResponse;
import me.shihab.revolut.exception.FailureStatusCode;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.mapper.TransactionMapper;
import me.shihab.revolut.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class TransferServiceImpl implements TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceImpl.class);

    private AccountDAO accountDao;

    private TransactionDAO transactionDAO;

    private TransactionMapper transactionMapper;

    public TransferServiceImpl(AccountDAO accountDAO, TransactionDAO transactionDAO, TransactionMapper transactionMapper) {
        this.accountDao = accountDAO;
        this.transactionDAO = transactionDAO;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TransferDTO transfer(TransferDTO transferDTO) throws RuntimeException {
        BigDecimal transferAmount = transferDTO.getAmount();

        AccountEntity fromAccount = accountDao.findById(transferDTO.getFromAccountId());
        if (fromAccount == null) {
            FailureResponse failureResponse = getFailureResponseForNoSourceAccountException();
            throw new RuntimeException(failureResponse);
        }

        AccountEntity toAccount = accountDao.findById(transferDTO.getToAccountId());
        if (toAccount == null) {
            FailureResponse failureResponse = getFailureResponseForNoDestinationAccountException();
            throw new RuntimeException(failureResponse);
        }

        if (!userHasSufficientBalance(fromAccount.getBalance(), transferAmount)) {
            FailureResponse failureResponse = getFailureResponseForNotSufficientBalance();
            throw new RuntimeException(failureResponse);
        }

        TransactionEntity transaction = doTransfer(fromAccount, toAccount, transferDTO);
        LOGGER.info("Transaction Successful. Transaction Details : " + transaction.toString());

        return transactionMapper.entityToDto(transaction);
    }


    private TransactionEntity doTransfer(AccountEntity fromAccount, AccountEntity toAccount, TransferDTO transferDTO) {
        BigDecimal transferAmount = transferDTO.getAmount();
        debitAccount(fromAccount, transferAmount);
        creditAccount(toAccount, transferAmount);

        TransactionEntity transaction = new TransactionEntity(fromAccount.getId(),
                toAccount.getId(),
                transferAmount);
        return transactionDAO.upsert(transaction);
    }

    private boolean userHasSufficientBalance(BigDecimal userBalance, BigDecimal amountToTransfer) {
        return (userBalance.subtract(amountToTransfer).compareTo(BigDecimal.ZERO) >= 0);
    }

    private AccountEntity debitAccount(AccountEntity account, BigDecimal debitAmount) {
        BigDecimal newBalance = account.getBalance().subtract(debitAmount);
        account.setBalance(newBalance);

        return accountDao.upsert(account);
    }

    private AccountEntity creditAccount(AccountEntity account, BigDecimal creditAmount) {
        BigDecimal newBalance = account.getBalance().add(creditAmount);
        account.setBalance(newBalance);

        return accountDao.upsert(account);
    }


    private FailureResponse getFailureResponseForNoSourceAccountException() {
        String failureMessage = FailureMessage.SOURCE_ACCOUNT_NOT_FOUND.message();
        int statusCode = FailureStatusCode.SOURCE_ACCOUNT_NOT_FOUND.statusCode();
        return new FailureResponse(statusCode, failureMessage);
    }

    private FailureResponse getFailureResponseForNoDestinationAccountException() {
        String failureMessage = FailureMessage.DESTINATION_ACCOUNT_NOT_FOUND.message();
        int statusCode = FailureStatusCode.DESTINATION_ACCOUNT_NOT_FOUND.statusCode();
        return new FailureResponse(statusCode, failureMessage);
    }

    private FailureResponse getFailureResponseForNotSufficientBalance() {
        String failureMessage = FailureMessage.NOT_ENOUGH_BALANCE.message();
        int statusCode = FailureStatusCode.NOT_ENOUGH_BALANCE.statusCode();
        return new FailureResponse(statusCode, failureMessage);
    }
}
