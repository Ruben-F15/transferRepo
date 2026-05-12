package com.microservice.transferservice.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {

    boolean transferFunds(String sourceAccountId, String destinationAccountId, BigDecimal amount);
}
