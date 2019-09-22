package me.shihab.revolut.mapper;

import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.core.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setup() {
        transactionMapper = new TransactionMapper();
    }

    @Test
    void entityToDto_GivenValidTransactionEntity_CorrectlyMapsToTransferDTO() {
        TransactionEntity testTransactionEntity = new TransactionEntity(1l, 2l, BigDecimal.valueOf(100.00));
        TransferDTO mappedTransferDTO = transactionMapper.entityToDto(testTransactionEntity);

        assertEquals(testTransactionEntity.getFromAccountId(), mappedTransferDTO.getFromAccountId());
        assertEquals(testTransactionEntity.getToAccountId(), mappedTransferDTO.getToAccountId());
        assertEquals(testTransactionEntity.getAmount(), mappedTransferDTO.getAmount());
    }

    @Test
    void entityToDto_GivenNullEntity_ShouldReturnNull() {
        TransferDTO mappedTransferDTO = transactionMapper.entityToDto(null);

        assertNull(mappedTransferDTO);
    }
}