package com.microservice.transferservice.service;

import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.kafka.event.*;
import org.springframework.stereotype.Service;

@Service
public interface TransferService {
    void createTransfer(CreateTransferRequestDTO request);

    void handleFundsReservedEvent(FundsReservedEvent fundsReservedEvent);

    void handleFundsReservationFailedEvent(FundsReservationFailedEvent fundsReservationFailedEvent);

    void handleFundsDebitedEvent(FundsDebitedEvent fundsDebitedEvent);

    void handleFundsCreditedEvent(FundsCreditedEvent fundsCreditedEvent);

    void handleFundsDebitFailedEvent(FundsDebitFailedEvent fundsDebitFailedEvent);

    void handleFundsCreditFailedEvent(FundsCreditFailedEvent fundsCreditFailedEvent);
}
