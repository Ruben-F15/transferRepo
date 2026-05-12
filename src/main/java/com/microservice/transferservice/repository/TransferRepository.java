package com.microservice.transferservice.repository;

import com.microservice.transferservice.domain.model.TransferDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;

public interface TransferRepository extends MongoRepository<TransferDocument, String> {


}
