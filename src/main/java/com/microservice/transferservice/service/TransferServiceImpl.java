package com.microservice.transferservice.service;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.enums.TransferType;
import com.microservice.transferservice.domain.model.TransferDocument;
import com.microservice.transferservice.dto.CreateTransferRequestDTO;
import com.microservice.transferservice.exception.EqualSourceAndDestinationUserIdException;
import com.microservice.transferservice.kafka.event.TransferRequestedEvent;
import com.microservice.transferservice.kafka.producer.TransferEventProducer;
import com.microservice.transferservice.repository.TransferRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        log.info("::::::::::::::: authentication.getName: " + authentication.getName());

        if(authentication.getName().equals(request.destinationUserId())) {
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
                .destinationUserId(transferDocument.getDestinationUserId())
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
}
