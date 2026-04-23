package com.microservice.transferService.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransactionService {

    boolean transferFunds(String sourceAccountId, String destinationAccountId, BigDecimal amount);
}
