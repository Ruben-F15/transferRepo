package com.microservice.transferservice.kafka.event;

public record FundsCreditedEvent(
        String sourceUserId,
        String transactionId
) {
}
