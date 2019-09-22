package me.shihab.revolut.mapper;

import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.core.TransactionEntity;

public class TransactionMapper {

    public TransferDTO entityToDto(TransactionEntity transactionEntity) {
        if (transactionEntity == null) {
            return null;
        }
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromAccountId(transactionEntity.getFromAccountId());
        transferDTO.setToAccountId(transactionEntity.getToAccountId());
        transferDTO.setAmount(transactionEntity.getAmount());
        return transferDTO;
    }

}
