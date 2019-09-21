package me.shihab.revolut.service.impl;

import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.core.AccountEntity;
import me.shihab.revolut.db.AccountDAO;
import me.shihab.revolut.exception.FailureMessage;
import me.shihab.revolut.exception.FailureResponse;
import me.shihab.revolut.exception.FailureStatusCode;
import me.shihab.revolut.exception.RuntimeException;
import me.shihab.revolut.mapper.AccountMapper;
import me.shihab.revolut.service.AccountService;

public class AccountServiceImpl implements AccountService {
    private AccountDAO accountDao;

    private AccountMapper accountMapper;

    public AccountServiceImpl(AccountDAO accountDao, AccountMapper accountMapper) {
        this.accountDao = accountDao;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountDTO create(AccountDTO accountToBeCreated) {
        AccountEntity accountEntity = accountMapper.dtoToEntity(accountToBeCreated);
        AccountEntity createdAccount = accountDao.upsert(accountEntity);
        return accountMapper.entityToDto(createdAccount);
    }

    @Override
    public AccountDTO get(long accountId) throws RuntimeException {
        AccountEntity singleAccount = accountDao.findById(accountId);
        if (singleAccount == null) {
            FailureResponse failureResponse = getFailureResponseForAccountNotFoundException();
            throw new RuntimeException(failureResponse);
        }
        return accountMapper.entityToDto(singleAccount);
    }

    private FailureResponse getFailureResponseForAccountNotFoundException() {
        String failureMessage = FailureMessage.ACCOUNT_NOT_FOUND.message();
        int statusCode = FailureStatusCode.ACCOUNT_NOT_FOUND.statusCode();
        return new FailureResponse(statusCode, failureMessage);
    }


}
