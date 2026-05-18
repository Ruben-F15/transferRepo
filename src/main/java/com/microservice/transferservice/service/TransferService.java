package com.microservice.transferservice.service;

import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {

    boolean transferFunds(String sourceUserId, String destinationUserId, BigDecimal amount);

    void createTransfer(CreateTransferRequestDTO request);
}
