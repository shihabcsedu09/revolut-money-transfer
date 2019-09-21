package me.shihab.revolut.service;

import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.exception.RuntimeException;

public interface AccountService {
    AccountDTO create(AccountDTO account);

    AccountDTO get(long accountId) throws RuntimeException;
}
