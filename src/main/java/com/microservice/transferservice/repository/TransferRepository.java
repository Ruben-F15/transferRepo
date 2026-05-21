package com.microservice.transferservice.repository;

import com.microservice.transferservice.domain.enums.TransferStatus;
import com.microservice.transferservice.domain.model.TransferDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransferRepository extends MongoRepository<TransferDocument, String> {

    Optional<TransferDocument> findByTransactionId(String transactionId);
    Optional<TransferDocument> findByTransactionIdAndStatus(String transactionId, TransferStatus transferStatus);
}
