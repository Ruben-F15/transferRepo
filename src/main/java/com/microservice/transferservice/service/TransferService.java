package com.microservice.transferservice.service;

import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.kafka.event.FundsReservationFailedEvent;
import com.microservice.transferservice.kafka.event.FundsReservedEvent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface TransferService {
    void createTransfer(CreateTransferRequestDTO request);

    void handleFundsReservedEvent(FundsReservedEvent fundsReservedEvent);

    void handleFundsReservationFailedEvent(FundsReservationFailedEvent fundsReservationFailedEvent);
}
