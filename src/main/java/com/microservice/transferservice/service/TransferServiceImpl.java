package com.microservice.transferservice.service;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.enums.TransferType;
import com.microservice.transferservice.domain.model.TransferDocument;
import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.exception.EqualSourceAndDestinationUserIdException;
import com.microservice.transferservice.exception.TransferNotFoundException;
import com.microservice.transferservice.kafka.event.FundsReservationFailedEvent;
import com.microservice.transferservice.kafka.event.FundsReservedEvent;
import com.microservice.transferservice.kafka.event.TransferDebitRequestedEvent;
import com.microservice.transferservice.kafka.event.TransferRequestedEvent;
import com.microservice.transferservice.kafka.producer.TransferEventProducer;
import com.microservice.transferservice.repository.TransferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferEventProducer transferEventProducer;

    @Override
    public void createTransfer(CreateTransferRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        log.info("::::::::::::::: authentication.getName: {}", authentication.getName());

        if (authentication.getName().equals(request.destinationUserId())) {
            throw new EqualSourceAndDestinationUserIdException("Cannot transfer to same account");
        }

        TransferDocument transferDocument = TransferDocument.builder()
                .transferType(TransferType.INTERNAL_TRANSFER)
                .sourceUserId(authentication.getName())
                .destinationUserId(request.destinationUserId())
                .amount(request.amount())
                .currency("EUR")
                .status(TransferStatus.PENDING)
                .createdAt(Instant.now())
                .statusUpdatedAt(Instant.now())
                .transactionId(UUID.randomUUID().toString())
                .build();

        transferRepository.save(transferDocument);
        log.info("::::::::::::::: transferDocument saved with transactionId={}", transferDocument.getTransactionId());

        log.info("::::::::::::::: sending event TransferRequestedEvent");
        TransferRequestedEvent event = TransferRequestedEvent.builder()
                .sourceUserId(transferDocument.getSourceUserId())
                .amount(transferDocument.getAmount())
                .transactionId(transferDocument.getTransactionId())
                .build();

        try {
            transferEventProducer.sendTransferRequestEvent(event);
            log.info("::::::::::::::: event TransferRequestedEvent sent!!");
        } catch (Exception ex) {
            transferDocument.setStatus(TransferStatus.FAILED);
            transferDocument.setFailureReason("ERROR SENDING KAFKA: transferEventProducer.sendTransferRequestEvent");
            transferRepository.save(transferDocument);
            throw ex;
        }
    }

    @Override
    public void handleFundsReservedEvent(FundsReservedEvent fundsReservedEvent) {
        try {
            log.info(":::::::: buscando transferDocument desde handleFundsReservedEvent={}", fundsReservedEvent.transactionId());
            TransferDocument transferDocument = transferRepository.findByTransactionIdAndStatus(fundsReservedEvent.transactionId(), TransferStatus.PENDING)
                    .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsReservedEvent.transactionId()));
            log.info(":::::::: transferDocument encontrado={}", transferDocument.getTransactionId());

            transferDocument.setStatus(TransferStatus.FUNDS_RESERVED);
            transferDocument.setStatusUpdatedAt(Instant.now());

            transferRepository.save(transferDocument);
            log.info(":::::::: transferDocument Actualizado y guardado");

            TransferDebitRequestedEvent debitRequestedEvent = new TransferDebitRequestedEvent(
                    transferDocument.getSourceUserId(),
                    transferDocument.getAmount(),
                    transferDocument.getTransactionId()
            );

            log.info(":::::::: enviamos debitRequestedEvent {}", debitRequestedEvent);
            transferEventProducer.sendTransferDebitRequestEvent(debitRequestedEvent);
            log.info(":::::::: enviado debitRequestedEvent");
        } catch (DataAccessException ex) {
            log.error("::::: exception en handleFundsReservedEvent{}", ex.getMessage());
        }
    }

    @Override
    public void handleFundsReservationFailedEvent(FundsReservationFailedEvent fundsReservationFailedEvent) {
        try {
            log.info(":::::::: buscando transferDocument desde handleFundsReservationFailedEvent={}", fundsReservationFailedEvent.transactionId());
            TransferDocument transferDocument = transferRepository.findByTransactionId(fundsReservationFailedEvent.transactionId())
                    .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsReservationFailedEvent.transactionId()));
            log.info(":::::::: transferDocument encontrado ={}", transferDocument.getTransactionId());

            transferDocument.setStatus(TransferStatus.FAILED);
            transferDocument.setFailureReason(fundsReservationFailedEvent.failReason());
            transferDocument.setStatusUpdatedAt(Instant.now());
            transferRepository.save(transferDocument);
            log.info(":::::::: transferDocument Actualizado y guardado como FAILED");
            System.out.println("::::::::: TRANSFERENCIA FALLIDA ::::::::::::::");
            System.out.println("::::::::: AQUI AÑADIRIAMOS ALGUNA LOGICA PARA COMUNICARLO AL USUARIO :::::::::");

        } catch (DataAccessException ex) {
            log.error("::::: exception en handleFundsReservationFailedEvent{}", ex.getMessage());
        }
    }
}
