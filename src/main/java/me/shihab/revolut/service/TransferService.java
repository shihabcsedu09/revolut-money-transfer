package me.shihab.revolut.service;

import me.shihab.revolut.api.TransferDTO;
import me.shihab.revolut.exception.RuntimeException;

public interface TransferService {
    TransferDTO transfer(TransferDTO transferDTO) throws RuntimeException;
}
