package com.microservice.transferservice.kafka.event;

public record FundsDebitedEvent(
        String sourceUserId,
        String transactionId
) {
}
