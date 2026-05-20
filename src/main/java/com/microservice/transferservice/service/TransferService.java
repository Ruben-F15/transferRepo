package com.microservice.transferservice.service;

import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {
    void createTransfer(CreateTransferRequestDTO request);
}
