package com.microservice.transferService.service;

import com.microservice.transferService.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public boolean transferFunds(String sourceAccountId, String destinationAccountId, BigDecimal amount) {
        return false;
    }

}
