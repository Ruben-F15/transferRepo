package com.microservice.transferservice.kafka.event;

import java.math.BigDecimal;

public record TransferDebitRequestedEvent(
        String sourceUserId,
        BigDecimal amount,
        String transactionId
) {
}
