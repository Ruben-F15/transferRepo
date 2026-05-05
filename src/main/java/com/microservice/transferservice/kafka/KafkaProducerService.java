package com.microservice.transferservice.kafka;

import java.math.BigDecimal;

public interface KafkaProducerService {

    void publishTransferRequest(String sourceId, String destinationId, BigDecimal amount);
}
