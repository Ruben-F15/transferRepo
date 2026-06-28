package com.microservice.transferservice.service;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.enums.TransferType;
import com.microservice.transferservice.domain.model.TransferDocument;
import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.exception.EqualSourceAndDestinationUserIdException;
import com.microservice.transferservice.exception.TransferNotFoundException;
import com.microservice.transferservice.infrastructure.metrics.TransferMetrics;
import com.microservice.transferservice.kafka.event.*;
import com.microservice.transferservice.kafka.producer.TransferEventProducer;
import com.microservice.transferservice.repository.TransferRepository;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final TransferEventProducer transferEventProducer;
    private final TransferMetrics transferMetrics;

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
        transferMetrics.incrementTransferCreatedCounter();

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
        log.info(":::::::: buscando transferDocument desde handleFundsReservedEvent={}", fundsReservedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionIdAndStatus(fundsReservedEvent.transactionId(), TransferStatus.PENDING)
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsReservedEvent.transactionId()));
        log.info(":::::::: transferDocument encontrado={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.FUNDS_RESERVED);

        transferRepository.save(transferDocument);
        log.info("::::::::: transferDocument Actualizado y guardado como {}", TransferStatus.FUNDS_RESERVED);

        TransferDebitRequestedEvent debitRequestedEvent = new TransferDebitRequestedEvent(
                transferDocument.getSourceUserId(),
                transferDocument.getAmount(),
                transferDocument.getTransactionId()
        );

        log.info(":::::::: enviamos debitRequestedEvent {}", debitRequestedEvent);
        transferEventProducer.sendTransferDebitRequestEvent(debitRequestedEvent);
        log.info(":::::::: enviado debitRequestedEvent");

    }

    @Override
    public void handleFundsReservationFailedEvent(FundsReservationFailedEvent fundsReservationFailedEvent) {
        log.info(":::::::: buscando transferDocument desde handleFundsReservationFailedEvent={}", fundsReservationFailedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionId(fundsReservationFailedEvent.transactionId())
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsReservationFailedEvent.transactionId()));
        log.info(":::::::: transferDocument encontrado ={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.FAILED);
        transferDocument.setFailureReason(fundsReservationFailedEvent.failReason());

        transferRepository.save(transferDocument);
        transferMetrics.incrementTransferFailedCounter();
        transferMetrics.recordTransferDuration(transferDocument.processingDuration());
        log.info(":::::::: transferDocument Actualizado y guardado como {}", TransferStatus.FAILED);
        System.out.println("::::::::: TRANSFERENCIA FALLIDA ::::::::::::::");
        System.out.println("::::::::: AQUI AÑADIRIAMOS ALGUNA LOGICA PARA COMUNICARLO AL USUARIO :::::::::");
    }

    @Override
    public void handleFundsDebitedEvent(FundsDebitedEvent fundsDebitedEvent) {
        log.info(":::::::: buscando transferDocument desde handleFundsDebitedEvent={}", fundsDebitedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionId(fundsDebitedEvent.transactionId())
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsDebitedEvent.transactionId()));
        log.info("::::::: transferDocument encontrado ={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.DEBITED);

        transferRepository.save(transferDocument);
        log.info("::::::: transferDocument Actualizado y guardado como {}", TransferStatus.DEBITED);

        TransferCreditRequestedEvent creditRequestedEvent = new TransferCreditRequestedEvent(
                transferDocument.getSourceUserId(),
                transferDocument.getDestinationUserId(),
                transferDocument.getAmount(),
                transferDocument.getTransactionId()
        );

        log.info(":::::::: enviamos creditRequestedEvent {}", creditRequestedEvent);
        transferEventProducer.sendTransferCreditRequestEvent(creditRequestedEvent);
        log.info(":::::::: enviado creditRequestedEvent");
    }

    @Override
    public void handleFundsCreditedEvent(FundsCreditedEvent fundsCreditedEvent) {
        log.info(":::::::: buscando transferDocument desde handleFundsCreditedEvent={}", fundsCreditedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionId(fundsCreditedEvent.transactionId())
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsCreditedEvent.transactionId()));
        log.info(":::::::::: transferDocument encontrado ={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.COMPLETED);

        transferRepository.save(transferDocument);
        transferMetrics.incrementTransferCompletedCounter();
        transferMetrics.recordTransferDuration(transferDocument.processingDuration());
        log.info(":::::::::: transferDocument Actualizado y guardado como {}", TransferStatus.COMPLETED);
        System.out.println(":::::::::::::::::::: COMPLETED ::::::::::::::::::::::::::");
    }

    @Override
    public void handleFundsDebitFailedEvent(FundsDebitFailedEvent fundsDebitFailedEvent) {
        log.info(":::::::: buscando transferDocument desde handleFundsDebitFailedEvent={}", fundsDebitFailedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionId(fundsDebitFailedEvent.transactionId())
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsDebitFailedEvent.transactionId()));
        log.info(":::::::: transferDocument encontrado: ={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.DEBIT_FAILED);
        transferDocument.setFailureReason(fundsDebitFailedEvent.failReason());

        transferRepository.save(transferDocument);
        transferMetrics.incrementTransferFailedCounter();
        transferMetrics.recordTransferDuration(transferDocument.processingDuration());
        log.info(":::::::: transferDocument Actualizado y guardado como: {}", TransferStatus.DEBIT_FAILED);
        System.out.println("::::::::: TRANSFERENCIA FALLIDA ::::::::::::::");
        System.out.println("::::::::: AQUI AÑADIRIAMOS ALGUNA LOGICA PARA COMUNICARLO AL USUARIO :::::::::");
    }

    @Override
    public void handleFundsCreditFailedEvent(FundsCreditFailedEvent fundsCreditFailedEvent) {
        log.info(":::::::: buscando transferDocument desde handleFundsCreditFailedEvent={}", fundsCreditFailedEvent.transactionId());
        TransferDocument transferDocument = transferRepository.findByTransactionId(fundsCreditFailedEvent.transactionId())
                .orElseThrow(() -> new TransferNotFoundException("Transfer not found for TransactionId:" + fundsCreditFailedEvent.transactionId()));
        log.info(":::::::: transferDocument encontrado : ={}", transferDocument.getTransactionId());

        transferDocument.updateStatus(TransferStatus.CREDIT_FAILED);
        transferDocument.setFailureReason(fundsCreditFailedEvent.failReason());

        transferRepository.save(transferDocument);
        transferMetrics.incrementTransferFailedCounter();
        transferMetrics.recordTransferDuration(transferDocument.processingDuration());
        log.info("::::::::: transferDocument Actualizado y guardado como: {}", TransferStatus.CREDIT_FAILED);
        System.out.println("::::::::: TRANSFERENCIA FALLIDA ::::::::::::::");
        System.out.println("::::::::: AQUI AÑADIRIAMOS ALGUNA LOGICA PARA COMUNICARLO AL USUARIO :::::::::");
    }
}
