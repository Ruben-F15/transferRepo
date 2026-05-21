package com.microservice.transferservice.kafka.event;

import java.math.BigDecimal;

public record TransferCreditRequestedEvent(
        String destinationUserId,
        BigDecimal amount,
        String transactionId
) {
}
