package me.shihab.revolut.mapper;

import me.shihab.revolut.api.AccountDTO;
import me.shihab.revolut.core.AccountEntity;

public class AccountMapper {

    public AccountDTO entityToDto(AccountEntity accountEntity) {
        if (accountEntity == null) {
            return null;
        }
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(accountEntity.getId());
        accountDTO.setName(accountEntity.getName());
        accountDTO.setBalance(accountEntity.getBalance());
        return accountDTO;
    }

    public AccountEntity dtoToEntity(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }
        return new AccountEntity(accountDTO.getName(), accountDTO.getBalance());
    }
}
